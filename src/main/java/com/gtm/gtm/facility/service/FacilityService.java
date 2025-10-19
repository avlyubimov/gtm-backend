package com.gtm.gtm.facility.service;

import com.gtm.gtm.facility.domain.Facility;
import com.gtm.gtm.facility.dto.FacilityCreateDto;
import com.gtm.gtm.facility.dto.FacilityDto;
import com.gtm.gtm.facility.dto.FacilityUpdateDto;
import com.gtm.gtm.facility.repository.FacilityRepository;
import com.gtm.gtm.point.domain.PointType;
import com.gtm.gtm.point.repository.PointRepository;
import com.gtm.gtm.site.repository.SiteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository repo;
    private final SiteRepository siteRepo;
    private final PointRepository pointRepo;

    public Page<FacilityDto> list(Long siteId, String nameFilter, Pageable pageable) {
        Page<Facility> page;
        if (siteId != null) {
            page = repo.findAllBySite_Id(siteId, pageable);
        } else if (nameFilter != null && !nameFilter.isBlank()) {
            page = repo.findAllByNameIgnoreCaseContaining(nameFilter.trim(), pageable);
        } else {
            page = repo.findAll(pageable);
        }

        Set<Long> ids = page.getContent().stream().map(Facility::getId).collect(Collectors.toSet());
        var countsMap = buildCountsMap(ids);

        return page.map(f -> toDto(f, countsMap.get(f.getId())));
    }

    public FacilityDto get(Long id) {
        var f = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Facility not found"));

        var countsMap = buildCountsMap(Set.of(f.getId()));
        return toDto(f, countsMap.get(f.getId()));
    }

    @Transactional
    public FacilityDto create(FacilityCreateDto dto) {
        if (repo.existsBySite_IdAndCodeIgnoreCase(dto.siteId(), dto.code()))
            throw new IllegalArgumentException("Facility code already exists in this site");

        var siteRef = siteRepo.getReferenceById(dto.siteId());

        var f = new Facility();
        f.setSite(siteRef);
        f.setName(dto.name().trim());
        f.setCode(dto.code().trim());
        f.setCreatedAt(OffsetDateTime.now());
        f.setUpdatedAt(OffsetDateTime.now());

        var saved = repo.save(f);
        return toDto(saved, emptyTypeMap());
    }

    @Transactional
    public FacilityDto update(Long id, FacilityUpdateDto dto) {
        var f = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Facility not found"));

        Long siteId = f.getSite().getId();
        if (!f.getCode().equalsIgnoreCase(dto.code())
                && repo.existsBySite_IdAndCodeIgnoreCase(siteId, dto.code())) {
            throw new IllegalArgumentException("Facility code already exists in this site");
        }

        f.setName(dto.name().trim());
        f.setCode(dto.code().trim());
        f.setUpdatedAt(OffsetDateTime.now());

        var saved = repo.saveAndFlush(f);
        var countsMap = buildCountsMap(Set.of(saved.getId()));
        return toDto(saved, countsMap.get(saved.getId()));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Facility not found");
        repo.deleteById(id);
    }

    private Map<PointType, Long> emptyTypeMap() {
        var m = new EnumMap<PointType, Long>(PointType.class);
        for (PointType t : PointType.values()) m.put(t, 0L);
        return m;
    }

    private Map<Long, EnumMap<PointType, Long>> buildCountsMap(Set<Long> facilityIds) {
        var result = new HashMap<Long, EnumMap<PointType, Long>>();
        for (Long id : facilityIds) result.put(id, new EnumMap<>(emptyTypeMap()));

        for (var row : pointRepo.countByTypeForFacilities(facilityIds)) {
            var m = result.get(row.getFacilityId());
            if (m != null) m.put(row.getType(), row.getCnt());
        }
        return result;
    }

    private FacilityDto toDto(Facility f, Map<PointType, Long> byType) {
        long total = (byType == null) ? 0L : byType.values().stream().mapToLong(Long::longValue).sum();
        return new FacilityDto(
                f.getId(),
                f.getSite().getId(),
                f.getName(),
                f.getCode(),
                f.getCreatedAt(),
                f.getUpdatedAt(),
                total,
                byType == null ? Map.of() : byType
        );
    }
}
