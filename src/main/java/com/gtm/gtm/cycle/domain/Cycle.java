package com.gtm.gtm.cycle.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import com.gtm.gtm.facility.domain.Facility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "gtm_cycle",
        indexes = { @Index(name = "idx_cycle_facility", columnList = "facility_id") })
public class Cycle extends SoftDeletable {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(name = "period_start", nullable = false)
    private OffsetDateTime periodStart;

    @NotNull @Column(name = "period_end", nullable = false)
    private OffsetDateTime periodEnd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CycleStatus status;
}
