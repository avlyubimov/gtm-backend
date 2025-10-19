package com.gtm.gtm.cycle.dto;

import com.gtm.gtm.cycle.domain.CycleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CycleUpdateDto(
        @NotBlank String name,
        @NotNull OffsetDateTime periodStart,
        @NotNull OffsetDateTime periodEnd,
        @NotNull CycleStatus status
) {}
