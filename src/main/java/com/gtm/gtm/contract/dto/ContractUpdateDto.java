package com.gtm.gtm.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ContractUpdateDto(
        @NotBlank String number,
        @NotNull LocalDate signedAt,
        @NotBlank String customer,
        @NotBlank String customerFullName
) {}
