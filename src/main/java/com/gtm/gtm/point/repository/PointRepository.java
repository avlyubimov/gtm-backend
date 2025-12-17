package com.gtm.gtm.point.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.point.domain.Point;
import com.gtm.gtm.point.domain.PointType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PointRepository extends SoftDeleteRepository<Point, Long> {

    @EntityGraph(attributePaths = "facility")
    Page<Point> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "facility")
    Page<Point> findAllByFacility_Id(Long facilityId, Pageable pageable);

    @EntityGraph(attributePaths = "facility")
    Page<Point> findAllByType(PointType type, Pageable pageable);

    @EntityGraph(attributePaths = "facility")
    Page<Point> findAllByFacility_IdAndType(Long facilityId, PointType type, Pageable pageable);

    // ВАЖНО: переопределяем findById и тоже подгружаем facility
    @EntityGraph(attributePaths = "facility")
    Optional<Point> findById(Long id);

    @Query("""
        select p.facility.id as facilityId,
               p.type        as type,
               count(p)      as cnt
          from Point p
         where p.facility.id in :facilityIds
         group by p.facility.id, p.type
    """)
    List<FacilityTypeCountRow> countByTypeForFacilities(Collection<Long> facilityIds);

    interface FacilityTypeCountRow {
        Long getFacilityId();
        PointType getType();
        long getCnt();
    }

    @EntityGraph(attributePaths = "facility")
    List<Point> findAllByFacility_Id(Long facilityId);

    // Aggregation by Site
    @Query("""
        select p.facility.site.id as siteId,
               p.type              as type,
               count(p)            as cnt
          from Point p
         where p.facility.site.id in :siteIds
         group by p.facility.site.id, p.type
    """)
    List<SiteTypeCountRow> countByTypeForSites(Collection<Long> siteIds);

    interface SiteTypeCountRow {
        Long getSiteId();
        PointType getType();
        long getCnt();
    }
}
