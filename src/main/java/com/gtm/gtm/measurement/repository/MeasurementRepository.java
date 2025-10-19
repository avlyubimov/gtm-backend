package com.gtm.gtm.measurement.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.measurement.domain.Measurement;
import com.gtm.gtm.measurement.domain.MeasurementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface MeasurementRepository extends SoftDeleteRepository<Measurement, Long> {
    Page<Measurement> findAllByFacility_Id(Long facilityId, Pageable p);
    Page<Measurement> findAllByPoint_Id(Long pointId, Pageable p);
    Page<Measurement> findAllByCycle_Id(Long cycleId, Pageable p);
    Page<Measurement> findAllByPoint_IdAndType(Long pointId, MeasurementType type, Pageable p);

    Optional<Measurement> findFirstByPoint_IdAndTypeOrderByMeasuredAtDesc(Long pointId, MeasurementType type);
    @Query("""
           select m.point.id 
           from Measurement m
           where m.user.id = :userId 
             and m.cycle.id = :cycleId 
           """)
    Set<Long> findMeasuredPointIdsForUserAndCycle(@Param("userId") Long userId,
                                                  @Param("cycleId") Long cycleId);
}
