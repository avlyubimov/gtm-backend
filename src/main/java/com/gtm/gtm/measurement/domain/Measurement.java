package com.gtm.gtm.measurement.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import com.gtm.gtm.common.jpa.JsonbConverter;
import com.gtm.gtm.cycle.domain.Cycle;
import com.gtm.gtm.facility.domain.Facility;
import com.gtm.gtm.point.domain.Point;
import com.gtm.gtm.user.domain.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "gtm_measurement")
@SQLRestriction("is_deleted = false")
public class Measurement extends SoftDeletable {

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cycle_id", nullable = false)
    private Cycle cycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private MeasurementType type;

    @Convert(converter = JsonbConverter.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> payload;

    @Column(name = "measured_at", nullable = false)
    private OffsetDateTime measuredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
}
