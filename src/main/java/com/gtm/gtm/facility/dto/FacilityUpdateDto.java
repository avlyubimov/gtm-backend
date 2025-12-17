package com.gtm.gtm.facility.dto;

import jakarta.validation.constraints.NotBlank;

public record FacilityUpdateDto(
        @NotBlank String name,
        @NotBlank String code,
        Long parentId
) {}
