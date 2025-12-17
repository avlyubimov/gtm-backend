package com.gtm.gtm.assigment.dto;

public record AssignmentDto(
        Long id,
        Long siteId,
        String siteName,
        Long userId,
        String userName,
        boolean active
) {}
