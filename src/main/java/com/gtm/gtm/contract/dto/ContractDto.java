package com.gtm.gtm.contract.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ContractDto(
        Long id,
        String number,
        LocalDate signedAt,
        String customer,
        String customerFullName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long siteCount
) {}
