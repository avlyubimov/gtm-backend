package com.gtm.gtm.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "gtm_refresh_token", indexes = {
        @Index(name = "idx_refresh_jti", columnList = "jti", unique = true),
        @Index(name = "idx_refresh_user", columnList = "user_id")
})
@Getter
@Setter
public class RefreshToken {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist void pre() {
        if (id == null) id = UUID.randomUUID();
        createdAt = OffsetDateTime.now();
    }

    @JdbcTypeCode(SqlTypes.INET)
    @Column(columnDefinition = "inet")
    private InetAddress ip;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "device")
    private String device;
}
