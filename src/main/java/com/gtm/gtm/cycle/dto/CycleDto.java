package com.gtm.gtm.cycle.dto;

import com.gtm.gtm.cycle.domain.CycleStatus;

import java.time.OffsetDateTime;

public record CycleDto(
        Long id,
        String name,
        OffsetDateTime periodStart,
        OffsetDateTime periodEnd,
        Long facilityId,
        String facilityName,
        CycleStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
