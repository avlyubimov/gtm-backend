package com.gtm.gtm.site.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import com.gtm.gtm.contract.domain.Contract;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "gtm_site",
        uniqueConstraints = @UniqueConstraint(name = "uk_site_contract_code", columnNames = {"contract_id","code"}))
@Getter
@Setter
@SQLRestriction("is_deleted = false")
public class Site extends SoftDeletable {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Formula("(select count(*) from gtm_facility f where f.site_id = id and f.is_deleted = false)")
    private long facilityCount;
}

