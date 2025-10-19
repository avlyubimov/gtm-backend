package com.gtm.gtm.cycle.dto;

import com.gtm.gtm.cycle.domain.CycleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CycleCreateDto(
        @NotBlank String name,
        @NotNull OffsetDateTime periodStart,
        @NotNull OffsetDateTime periodEnd,
        @NotNull Long facilityId,
        @NotNull CycleStatus status
) {}
