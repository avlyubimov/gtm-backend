package com.gtm.gtm.site.dto;

import java.time.OffsetDateTime;

public record SiteDto(
        Long id,
        Long contractId,
        String name,
        String code,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long facilityCount
) {}
