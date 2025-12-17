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
import com.gtm.gtm.assigment.dto.MySiteDto;
import com.gtm.gtm.assigment.dto.TodoPointDto;
import com.gtm.gtm.assigment.repository.AssignmentRepository;
import com.gtm.gtm.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import com.gtm.gtm.common.error.ConflictException;
import com.gtm.gtm.common.error.NotFoundException;
import com.gtm.gtm.site.repository.SiteRepository;
import com.gtm.gtm.point.domain.PointType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository repo;
    private final FacilityRepository facilityRepo;
    private final SiteRepository siteRepo;
    private final AppUserRepository userRepo;
    private final MeasurementRepository measurementRepo;
    private final PointRepository pointRepo;
    private final CycleRepository cycleRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AssignmentDto assign(AssignmentCreateDto dto) {
        var site = siteRepo.findById(dto.siteId())
                .orElseThrow(() -> new NotFoundException("Site not found"));
        var user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // На одном участке может быть только один активный камеральщик
        if (repo.existsBySite_IdAndActiveTrue(site.getId())) {
            throw new ConflictException("Site already assigned");
        }

        var a = new Assignment();
        a.setSite(site);
        a.setUser(user);
        a.setActive(true);
        a.setCreatedAt(OffsetDateTime.now());
        a.setUpdatedAt(OffsetDateTime.now());

        var saved = repo.save(a);

        // publish domain event after save (will be handled AFTER_COMMIT)
        eventPublisher.publishEvent(new com.gtm.gtm.notification.event.AssignmentCreatedEvent(
                saved.getId(), user.getId(), site.getId(), site.getName(), site.getCode()
        ));

        return toDto(saved);
    }

    @Transactional
    public void deactivate(Long id) {
        var a = repo.findById(id).orElseThrow(() -> new NotFoundException("Assignment not found"));
        if (!a.isActive()) return;
        a.setActive(false);
        a.setUpdatedAt(OffsetDateTime.now());
        var saved = repo.saveAndFlush(a);

        var site = saved.getSite();
        var user = saved.getUser();
        eventPublisher.publishEvent(new com.gtm.gtm.notification.event.AssignmentDeactivatedEvent(
                saved.getId(), user.getId(), site.getId(), site.getName(), site.getCode()
        ));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Assignment not found");
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MySiteDto> mySites(Long userId, org.springframework.data.domain.Pageable pageable) {
        var page = repo.findAllByUser_IdAndActiveTrue(userId, pageable);

        // Соберём ID участков страницы
        var siteIds = page.getContent().stream().map(a -> a.getSite().getId()).collect(java.util.stream.Collectors.toSet());

        // Посчитаем точки по типам для участков
        var pointsBySite = buildSitePointsMap(siteIds);

        return page.map(a -> {
            var s = a.getSite();
            var typeMap = pointsBySite.getOrDefault(s.getId(), emptyTypeMap());
            long total = typeMap.values().stream().mapToLong(Long::longValue).sum();
            var contract = s.getContract();
            return new MySiteDto(
                    a.getId(), s.getId(), s.getName(), s.getCode(),
                    contract.getId(), contract.getNumber(),
                    s.getFacilityCount(),
                    total,
                    typeMap,
                    a.isActive(), a.getCreatedAt(), a.getUpdatedAt()
            );
        });
    }

    @Transactional(readOnly = true)
    public List<TodoPointDto> todoPoints(Long userId, Long facilityId) {
        var facility = facilityRepo.findById(facilityId)
                .orElseThrow(() -> new NotFoundException("Facility not found"));

        // Проверяем назначение на участок, к которому относится объект
        repo.findByUser_IdAndSite_IdAndActiveTrue(userId, facility.getSite().getId())
                .orElseThrow(() -> new NotFoundException("No active assignment for this facility's site"));

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
                a.getSite().getId(),
                a.getSite().getName(),
                a.getUser().getId(),
                a.getUser().getFullName(),
                a.isActive()
        );
    }

    private EnumMap<PointType, Long> emptyTypeMap() {
        var m = new EnumMap<PointType, Long>(PointType.class);
        for (PointType t : PointType.values()) m.put(t, 0L);
        return m;
    }

    private Map<Long, EnumMap<PointType, Long>> buildSitePointsMap(Set<Long> siteIds) {
        var result = new java.util.HashMap<Long, EnumMap<PointType, Long>>();
        for (Long id : siteIds) result.put(id, emptyTypeMap());
        for (var row : pointRepo.countByTypeForSites(siteIds)) {
            var m = result.get(row.getSiteId());
            if (m != null) m.put(row.getType(), row.getCnt());
        }
        return result;
    }
}
