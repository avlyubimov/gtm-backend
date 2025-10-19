package com.gtm.gtm.facility.dto;

import com.gtm.gtm.point.domain.PointType;

import java.time.OffsetDateTime;
import java.util.Map;

public record FacilityDto(
        Long id,
        Long siteId,
        String name,
        String code,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long pointTotal,
        Map<PointType, Long> pointsByType
) {}
