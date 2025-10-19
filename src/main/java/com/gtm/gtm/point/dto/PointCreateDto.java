package com.gtm.gtm.point.dto;

import com.gtm.gtm.point.domain.PointType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PointCreateDto(
        @NotBlank String name,
        @NotNull Long facilityId,
        @NotNull PointType type
) {}
