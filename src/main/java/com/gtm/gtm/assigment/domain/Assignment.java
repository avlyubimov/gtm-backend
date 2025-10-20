package com.gtm.gtm.assigment.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import com.gtm.gtm.facility.domain.Facility;
import com.gtm.gtm.user.domain.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "gtm_assignment",
        uniqueConstraints = @UniqueConstraint(name = "uq_assignment_unique", columnNames = {"facility_id","user_id","active"}))
@Getter
@Setter
public class Assignment extends SoftDeletable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private boolean active = true;
}
