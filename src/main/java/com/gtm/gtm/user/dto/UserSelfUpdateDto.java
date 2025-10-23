package com.gtm.gtm.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserSelfUpdateDto(
        @Schema(example = "Иванов Иван") @NotBlank String fullName,
        @Schema(example = "+77011234567")
        @Pattern(regexp = "^\\+\\d{7,15}$", message = "Phone must be in E.164, e.g. +77011234567")
        String phone,
        @Schema(example = "ivan@example.com") @Email @NotBlank String email,
        @Schema(example = "1990-05-20") LocalDate dateOfBirth,
        @Schema(example = "https://cdn.example.com/u/1.jpg") String photoUrl
) {}
