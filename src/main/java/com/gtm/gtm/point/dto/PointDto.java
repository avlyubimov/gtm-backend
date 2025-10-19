package com.gtm.gtm.point.dto;

import com.gtm.gtm.point.domain.PointType;

import java.time.OffsetDateTime;

public record PointDto(
        Long id,
        String name,
        Long facilityId,
        String facilityName,
        PointType type,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
