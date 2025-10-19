package com.gtm.gtm.point.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.point.domain.Point;
import com.gtm.gtm.point.domain.PointType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface PointRepository extends SoftDeleteRepository<Point, Long> {
    Page<Point> findAll(Pageable pageable);
    Page<Point> findAllByFacility_Id(Long facilityId, Pageable pageable);
    Page<Point> findAllByType(PointType type, Pageable pageable);
    Page<Point> findAllByFacility_IdAndType(Long facilityId, PointType type, Pageable pageable);

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

    List<Point> findAllByFacility_Id(Long facilityId);
}
