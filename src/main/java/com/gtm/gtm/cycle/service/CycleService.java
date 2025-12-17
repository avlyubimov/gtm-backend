package com.gtm.gtm.cycle.service;

import com.gtm.gtm.cycle.domain.Cycle;
import com.gtm.gtm.cycle.domain.CycleStatus;
import com.gtm.gtm.cycle.dto.CycleCreateDto;
import com.gtm.gtm.cycle.dto.CycleDto;
import com.gtm.gtm.cycle.dto.CycleUpdateDto;
import com.gtm.gtm.cycle.repository.CycleRepository;
import com.gtm.gtm.facility.repository.FacilityRepository;
import com.gtm.gtm.common.error.ConflictException;
import com.gtm.gtm.common.error.NotFoundException;
import com.gtm.gtm.common.error.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CycleService {
    private final CycleRepository repo;
    private final FacilityRepository facilityRepo;

    public Page<CycleDto> list(Long facilityId, CycleStatus status, Pageable pageable) {
        if (facilityId != null) return repo.findAllByFacility_Id(facilityId, pageable).map(CycleService::toDto);
        if (status != null)     return repo.findAllByStatus(status, pageable).map(CycleService::toDto);
        return repo.findAll(pageable).map(CycleService::toDto);
    }

    public CycleDto get(Long id) {
        return repo.findById(id).map(CycleService::toDto)
                .orElseThrow(() -> new NotFoundException("Cycle not found"));
    }

    @Transactional
    public CycleDto create(CycleCreateDto dto) {
        validate(dto.periodStart(), dto.periodEnd());
        var facility = facilityRepo.findById(dto.facilityId())
                .orElseThrow(() -> new NotFoundException("Facility not found"));

        var now = OffsetDateTime.now();
        var c = new Cycle();
        c.setName(dto.name().trim());
        c.setPeriodStart(dto.periodStart());
        c.setPeriodEnd(dto.periodEnd());
        c.setFacility(facility);
        c.setStatus(dto.status());
        c.setCreatedAt(now); c.setUpdatedAt(now);

        if (dto.status() == CycleStatus.ACTIVE) repo.closeActiveByFacility(facility.getId());

        return toDto(repo.save(c));
    }

    @Transactional
    public CycleDto update(Long id, CycleUpdateDto dto) {
        validate(dto.periodStart(), dto.periodEnd());
        var c = repo.findById(id).orElseThrow(() -> new NotFoundException("Cycle not found"));

        if (dto.status() == CycleStatus.ACTIVE && c.getStatus() != CycleStatus.ACTIVE) {
            repo.closeActiveByFacility(c.getFacility().getId());
        }

        c.setName(dto.name().trim());
        c.setPeriodStart(dto.periodStart());
        c.setPeriodEnd(dto.periodEnd());
        c.setStatus(dto.status());
        c.setUpdatedAt(OffsetDateTime.now());

        return toDto(repo.saveAndFlush(c));
    }

    @Transactional
    public void delete(Long id) { repo.softDeleteById(id); }

    @Transactional
    public CycleDto activate(Long id) {
        var c = repo.findById(id).orElseThrow(() -> new NotFoundException("Cycle not found"));
        repo.closeActiveByFacility(c.getFacility().getId());
        c.setStatus(CycleStatus.ACTIVE);
        c.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.saveAndFlush(c));
    }

    private static void validate(OffsetDateTime from, OffsetDateTime to) {
        if (!to.isAfter(from)) throw new ValidationException("periodEnd must be after periodStart");
    }

    private static CycleDto toDto(Cycle c) {
        return new CycleDto(
                c.getId(), c.getName(), c.getPeriodStart(), c.getPeriodEnd(),
                c.getFacility().getId(), c.getFacility().getName(),
                c.getStatus(), c.getCreatedAt(), c.getUpdatedAt()
        );
    }

    @Transactional
    public CycleDto changeStatus(Long id, CycleStatus newStatus) {
        var c = repo.findById(id).orElseThrow(() -> new NotFoundException("Cycle not found"));
        if (c.getStatus() == newStatus) return toDto(c);

        if (newStatus == CycleStatus.ACTIVE) {
            repo.closeActiveByFacility(c.getFacility().getId());
        }

        c.setStatus(newStatus);
        c.setUpdatedAt(OffsetDateTime.now());

        try {
            return toDto(repo.saveAndFlush(c));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Another ACTIVE cycle already exists for this facility", e);
        }
    }
}
