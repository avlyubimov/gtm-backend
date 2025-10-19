package com.gtm.gtm.site.dto;

import jakarta.validation.constraints.NotBlank;

public record SiteUpdateDto(
        @NotBlank String name,
        @NotBlank String code
) {}
