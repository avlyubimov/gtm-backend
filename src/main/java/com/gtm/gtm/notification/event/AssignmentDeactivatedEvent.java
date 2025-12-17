package com.gtm.gtm.notification.event;

public record AssignmentDeactivatedEvent(
        Long assignmentId,
        Long userId,
        Long siteId,
        String siteName,
        String siteCode
) {}
