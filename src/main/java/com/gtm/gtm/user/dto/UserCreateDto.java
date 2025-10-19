package com.gtm.gtm.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Set;

public record UserCreateDto(
        @Schema(example = "Иванов Иван") String fullName,
        @Schema(description = "Телефон в формате E.164", example = "+79991234567") String phone,
        @Schema(example = "ivan@example.com") String email,
        @Schema(example = "ivan") String username,
        @Schema(example = "ChangeMe123!") String password,
        Set<String> roles,
        @Schema(example = "1990-05-20") LocalDate dateOfBirth
) {
}
