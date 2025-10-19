package com.gtm.gtm.cycle.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.cycle.domain.Cycle;
import com.gtm.gtm.cycle.domain.CycleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CycleRepository extends SoftDeleteRepository<Cycle, Long> {
    Page<Cycle> findAllByFacility_Id(Long facilityId, Pageable pageable);
    Page<Cycle> findAllByStatus(CycleStatus status, Pageable pageable);
    Optional<Cycle> findFirstByFacility_IdAndStatus(Long facilityId, CycleStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Cycle c set c.status = 'CLOSED' where c.facility.id = :facilityId and c.status = 'ACTIVE'")
    int closeActiveByFacility(Long facilityId);
}
