package com.gtm.gtm.user.dto;

import com.gtm.gtm.user.domain.UserRole;
import com.gtm.gtm.user.domain.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Set;

public record UserAdminUpdateDto(
        @NotBlank String fullName,
        @Pattern(regexp = "^\\+\\d{7,15}$", message = "Phone must be in E.164") String phone,
        @Email @NotBlank String email,
        @NotBlank String username,
        LocalDate dateOfBirth,
        String photoUrl,
        Set<UserRole> roles,
        UserStatus status
) {}
