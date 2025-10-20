package com.gtm.gtm.point.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import com.gtm.gtm.facility.domain.Facility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "gtm_point",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_point_facility_type_name",
                        columnNames = {"facility_id","type","name"})
        })
public class Point extends SoftDeletable {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private PointType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
}
