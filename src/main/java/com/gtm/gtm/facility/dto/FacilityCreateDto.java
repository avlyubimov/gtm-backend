package com.gtm.gtm.facility.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FacilityCreateDto(
        @NotNull Long siteId,
        @NotBlank String name,
        @NotBlank String code,
        Long parentId
) {}
