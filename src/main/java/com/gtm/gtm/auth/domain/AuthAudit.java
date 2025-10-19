package com.gtm.gtm.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.OffsetDateTime;

@Entity
@Table(name = "gtm_auth_audit")
@Getter
@Setter
public class AuthAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    Long userId;
    String login;
    @JdbcTypeCode(SqlTypes.INET)
    @Column(columnDefinition = "inet")
    private InetAddress ip;
    String userAgent;
    boolean success;
    String error;
    OffsetDateTime createdAt = OffsetDateTime.now();
}
