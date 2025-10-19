package com.gtm.gtm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record RegisterDto(
        @Schema(example = "Иванов Иван") @NotBlank String fullName,
        @Schema(example = "+79991234567") @NotBlank String phone,
        @Schema(example = "ivan@example.com") @Email @NotBlank String email,
        @Schema(example = "ivan") @NotBlank String username,
        @Schema(example = "ChangeMe123!") @NotBlank String password,
        @Schema(example = "1990-05-20") LocalDate dateOfBirth
) {}
