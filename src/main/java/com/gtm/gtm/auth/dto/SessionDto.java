package com.gtm.gtm.auth.dto;

import java.net.InetAddress;

public record SessionDto(
        String jti,
        java.time.OffsetDateTime createdAt,
        java.time.OffsetDateTime expiresAt,
        InetAddress ip,
        String device,
        String userAgent,
        boolean revoked
) {}
