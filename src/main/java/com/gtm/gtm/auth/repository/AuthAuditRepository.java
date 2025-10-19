package com.gtm.gtm.auth.repository;

import com.gtm.gtm.auth.domain.AuthAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAuditRepository extends JpaRepository<AuthAudit, Long> { }
