package com.gtm.gtm.contract.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "gtm_contract")
@Getter
@Setter
public class Contract extends SoftDeletable {

    @Column(nullable = false, unique = true)
    private String number;

    @Column(name = "signed_at", nullable = false)
    private LocalDate signedAt;

    @Column(nullable = false)
    private String customer;

    @Column(name = "customer_full_name", nullable = false)
    private String customerFullName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Formula("(select count(*) from gtm_site s where s.contract_id = id)")
    private long siteCount;
}
