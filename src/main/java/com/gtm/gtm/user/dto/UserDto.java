package com.gtm.gtm.user.dto;

import com.gtm.gtm.user.domain.UserStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

public record UserDto(
        Long id,
        String fullName,
        String phone,
        String email,
        String username,
        Set<String> roles,
        UserStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime lastLoginAt,
        LocalDate dateOfBirth,
        Integer age
) {
}
