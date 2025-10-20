package com.gtm.gtm.assigment.dto;

public record TodoPointDto(
        Long pointId,
        String pointName,
        String type,
        Long cycleId,
        String cycleName,
        boolean measured
) {}
