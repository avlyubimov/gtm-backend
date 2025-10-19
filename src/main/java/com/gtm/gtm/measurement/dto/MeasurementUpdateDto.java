package com.gtm.gtm.measurement.dto;

import com.gtm.gtm.measurement.domain.MeasurementType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Map;

public record MeasurementUpdateDto(
        @NotNull MeasurementType type,
        @NotNull Map<String, Object> payload,
        OffsetDateTime measuredAt
) {}
