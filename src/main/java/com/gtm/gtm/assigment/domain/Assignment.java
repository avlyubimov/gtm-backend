package com.gtm.gtm.assigment.domain;

import com.gtm.gtm.common.domain.SoftDeletable;
import com.gtm.gtm.site.domain.Site;
import com.gtm.gtm.user.domain.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "gtm_assignment")
@Getter
@Setter
@SQLRestriction("is_deleted = false")
public class Assignment extends SoftDeletable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private boolean active = true;
}
