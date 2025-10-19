package com.gtm.gtm.measurement.dto;

import com.gtm.gtm.measurement.domain.MeasurementType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Map;

public record MeasurementCreateDto(
        @NotNull Long facilityId,
        @NotNull Long pointId,
        @NotNull Long cycleId,
        @NotNull MeasurementType type,
        @NotNull Map<String, Object> payload,
        OffsetDateTime measuredAt
) {}
