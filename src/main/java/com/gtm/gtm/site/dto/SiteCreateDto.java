package com.gtm.gtm.site.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SiteCreateDto(
        @NotNull Long contractId,
        @NotBlank String name,
        @NotBlank String code
) {}
