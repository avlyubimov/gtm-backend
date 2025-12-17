package com.gtm.gtm.measurement.service;

import com.gtm.gtm.cycle.repository.CycleRepository;
import com.gtm.gtm.facility.repository.FacilityRepository;
import com.gtm.gtm.measurement.domain.Measurement;
import com.gtm.gtm.measurement.domain.MeasurementType;
import com.gtm.gtm.measurement.dto.MeasurementCreateDto;
import com.gtm.gtm.measurement.dto.MeasurementDto;
import com.gtm.gtm.measurement.dto.MeasurementUpdateDto;
import com.gtm.gtm.common.error.NotFoundException;
import com.gtm.gtm.measurement.repository.MeasurementRepository;
import com.gtm.gtm.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final MeasurementRepository repo;
    private final FacilityRepository facilityRepo;
    private final PointRepository pointRepo;
    private final CycleRepository cycleRepo;

    public Page<MeasurementDto> list(Long facilityId, Long pointId, Long cycleId, MeasurementType type, Pageable p) {
        if (pointId != null && type != null) return repo.findAllByPoint_IdAndType(pointId, type, p).map(this::toDto);
        if (pointId != null) return repo.findAllByPoint_Id(pointId, p).map(this::toDto);
        if (cycleId != null) return repo.findAllByCycle_Id(cycleId, p).map(this::toDto);
        if (facilityId != null) return repo.findAllByFacility_Id(facilityId, p).map(this::toDto);
        return repo.findAll(p).map(this::toDto);
    }

    public MeasurementDto get(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Measurement not found"));
    }

    @Transactional
    public MeasurementDto create(MeasurementCreateDto dto) {
        var m = new Measurement();
        m.setFacility(facilityRepo.getReferenceById(dto.facilityId()));
        m.setPoint(pointRepo.getReferenceById(dto.pointId()));
        m.setCycle(cycleRepo.getReferenceById(dto.cycleId()));
        m.setType(dto.type());
        m.setPayload(dto.payload());
        m.setMeasuredAt(dto.measuredAt() != null ? dto.measuredAt() : OffsetDateTime.now());
        m.setCreatedAt(OffsetDateTime.now());
        m.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.save(m));
    }

    @Transactional
    public MeasurementDto update(Long id, MeasurementUpdateDto dto) {
        var m = repo.findById(id).orElseThrow(() -> new NotFoundException("Measurement not found"));
        m.setType(dto.type());
        m.setPayload(dto.payload());
        if (dto.measuredAt() != null) m.setMeasuredAt(dto.measuredAt());
        m.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.saveAndFlush(m));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private MeasurementDto toDto(Measurement m) {
        return new MeasurementDto(
                m.getId(),
                m.getFacility().getId(),
                m.getPoint().getId(),
                m.getCycle().getId(),
                m.getType(),
                m.getPayload(),
                m.getMeasuredAt(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}

