package com.gtm.gtm.user.assigment.dto;

public record AssignmentDto(
        Long id,
        Long facilityId,
        String facilityName,
        Long userId,
        String userName,
        boolean active
) {}
