package com.gtm.gtm.point.service;

import com.gtm.gtm.facility.repository.FacilityRepository;
import com.gtm.gtm.point.domain.Point;
import com.gtm.gtm.point.domain.PointType;
import com.gtm.gtm.point.dto.PointCreateDto;
import com.gtm.gtm.point.dto.PointDto;
import com.gtm.gtm.point.dto.PointUpdateDto;
import com.gtm.gtm.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository repo;
    private final FacilityRepository facilityRepo;

    @Transactional
    public PointDto create(PointCreateDto dto) {
        var facility = facilityRepo.findById(dto.facilityId())
                .orElseThrow(() -> new IllegalArgumentException("Facility not found"));

        var now = OffsetDateTime.now();

        var p = new Point();
        p.setName(dto.name().trim());
        p.setType(dto.type());
        p.setFacility(facility);
        p.setCreatedAt(now);
        p.setUpdatedAt(now);

        return toDto(repo.save(p));
    }

    public PointDto get(Long id) {
        var p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Point not found"));
        return toDto(p);
    }

    public Page<PointDto> list(Long facilityId, PointType type, Pageable pageable) {
        Page<Point> page;
        if (facilityId != null && type != null) {
            page = repo.findAllByFacility_IdAndType(facilityId, type, pageable);
        } else if (facilityId != null) {
            page = repo.findAllByFacility_Id(facilityId, pageable);
        } else if (type != null) {
            page = repo.findAllByType(type, pageable);
        } else {
            page = repo.findAll(pageable);
        }
        return page.map(PointService::toDto);
    }

    @Transactional
    public PointDto update(Long id, PointUpdateDto dto) {
        var p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Point not found"));
        p.setName(dto.name().trim());
        p.setType(dto.type());
        p.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        repo.softDeleteById(id);
    }

    @Transactional
    public void restore(Long id) {
        repo.restoreById(id);
    }

    private static PointDto toDto(Point p) {
        return new PointDto(
                p.getId(),
                p.getName(),
                p.getFacility().getId(),
                p.getFacility().getName(),
                p.getType(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
