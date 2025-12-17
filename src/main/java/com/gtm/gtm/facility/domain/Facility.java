package com.gtm.gtm.facility.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import com.gtm.gtm.site.domain.Site;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "gtm_facility",
        uniqueConstraints = @UniqueConstraint(name = "uk_facility_site_code", columnNames = {"site_id","code"}))
@Getter
@Setter
@SQLRestriction("is_deleted = false")
public class Facility extends SoftDeletable {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Facility parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private java.util.Set<Facility> children = new java.util.LinkedHashSet<>();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
