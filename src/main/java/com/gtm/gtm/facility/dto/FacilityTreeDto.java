package com.gtm.gtm.facility.dto;

import com.gtm.gtm.point.domain.PointType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record FacilityTreeDto(
        Long id,
        Long siteId,
        Long parentId,
        String name,
        String code,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long pointTotal,
        Map<PointType, Long> pointsByType,
        List<FacilityTreeDto> children
) {}
