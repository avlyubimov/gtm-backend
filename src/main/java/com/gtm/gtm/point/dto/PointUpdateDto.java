package com.gtm.gtm.point.dto;

import com.gtm.gtm.point.domain.PointType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PointUpdateDto(
        @NotBlank String name,
        @NotNull PointType type
) {}
