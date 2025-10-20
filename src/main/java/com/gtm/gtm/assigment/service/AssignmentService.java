package com.gtm.gtm.assigment.service;

import com.gtm.gtm.cycle.domain.Cycle;
import com.gtm.gtm.cycle.domain.CycleStatus;
import com.gtm.gtm.cycle.repository.CycleRepository;
import com.gtm.gtm.facility.repository.FacilityRepository;
import com.gtm.gtm.measurement.repository.MeasurementRepository;
import com.gtm.gtm.point.repository.PointRepository;
import com.gtm.gtm.assigment.domain.Assignment;
import com.gtm.gtm.assigment.dto.AssignmentCreateDto;
import com.gtm.gtm.assigment.dto.AssignmentDto;
import com.gtm.gtm.assigment.dto.TodoPointDto;
import com.gtm.gtm.assigment.repository.AssignmentRepository;
import com.gtm.gtm.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository repo;
    private final FacilityRepository facilityRepo;
    private final AppUserRepository userRepo;
    private final MeasurementRepository measurementRepo;
    private final PointRepository pointRepo;
    private final CycleRepository cycleRepo;

    @Transactional
    public AssignmentDto assign(AssignmentCreateDto dto) {
        var facility = facilityRepo.findById(dto.facilityId())
                .orElseThrow(() -> new IllegalArgumentException("Facility not found"));
        var user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (repo.existsByFacility_IdAndUser_IdAndActiveTrue(facility.getId(), user.getId())) {
            throw new IllegalArgumentException("Assignment already active for this pair");
        }

        var a = new Assignment();
        a.setFacility(facility);
        a.setUser(user);
        a.setActive(true);
        a.setCreatedAt(OffsetDateTime.now());
        a.setUpdatedAt(OffsetDateTime.now());

        return toDto(repo.save(a));
    }

    @Transactional
    public void deactivate(Long id) {
        var a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        if (!a.isActive()) return;
        a.setActive(false);
        a.setUpdatedAt(OffsetDateTime.now());
        repo.saveAndFlush(a);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Assignment not found");
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> myFacilities(Long userId) {
        return repo.findAllByUser_IdAndActiveTrue(userId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TodoPointDto> todoPoints(Long userId, Long facilityId) {
        repo.findByUser_IdAndFacility_IdAndActiveTrue(userId, facilityId)
                .orElseThrow(() -> new IllegalArgumentException("No active assignment for this facility"));

        var activeCycleOpt = cycleRepo.findFirstByFacility_IdAndStatus(
                facilityId, CycleStatus.ACTIVE);

        Set<Long> measuredPointIds = activeCycleOpt
                .map(c -> measurementRepo.findMeasuredPointIdsForUserAndCycle(userId, c.getId()))
                .orElseGet(Set::of);

        var points = pointRepo.findAllByFacility_Id(facilityId);

        return points.stream()
                .map(p -> new TodoPointDto(
                        p.getId(),
                        p.getName(),
                        p.getType().name(),
                        activeCycleOpt.map(Cycle::getId).orElse(null),
                        activeCycleOpt.map(Cycle::getName).orElse(null),
                        measuredPointIds.contains(p.getId())
                ))
                .toList();
    }

    private AssignmentDto toDto(Assignment a) {
        return new AssignmentDto(
                a.getId(),
                a.getFacility().getId(),
                a.getFacility().getName(),
                a.getUser().getId(),
                a.getUser().getFullName(),
                a.isActive()
        );
    }
}
