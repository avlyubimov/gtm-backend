package com.gtm.gtm.notification.event;

public record AssignmentCreatedEvent(
        Long assignmentId,
        Long userId,
        Long siteId,
        String siteName,
        String siteCode
) {}
