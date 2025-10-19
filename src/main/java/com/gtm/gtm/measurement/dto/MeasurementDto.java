package com.gtm.gtm.measurement.dto;

import com.gtm.gtm.measurement.domain.MeasurementType;

import java.time.OffsetDateTime;
import java.util.Map;

public record MeasurementDto(
        Long id,
        Long facilityId,
        Long pointId,
        Long cycleId,
        MeasurementType type,
        Map<String, Object> payload,
        OffsetDateTime measuredAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

