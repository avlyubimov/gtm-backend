package com.gtm.gtm.user.dto;

import com.gtm.gtm.user.domain.UserRole;
import com.gtm.gtm.user.domain.UserStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

public record UserFilter(
        String q,

        String email,
        String username,
        String phone,

        Set<UserRole> roles,
        UserStatus status,

        Boolean hasPhoto,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime updatedFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime updatedTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime lastLoginFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime lastLoginTo,

        Integer ageFrom,
        Integer ageTo,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dobFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dobTo
) {}
