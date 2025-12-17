package com.gtm.gtm.assigment.dto;

import com.gtm.gtm.point.domain.PointType;

import java.time.OffsetDateTime;
import java.util.Map;

public record MySiteDto(
        Long assignmentId,
        Long siteId,
        String siteName,
        String siteCode,
        Long contractId,
        String contractNumber,
        long facilityCount,
        long pointTotal,
        Map<PointType, Long> pointsByType,
        boolean active,
        OffsetDateTime assignedAt,
        OffsetDateTime updatedAt
) {}
