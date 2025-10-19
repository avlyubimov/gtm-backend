package com.gtm.gtm.facility.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.facility.domain.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FacilityRepository extends SoftDeleteRepository<Facility, Long> {
    boolean existsBySite_IdAndCodeIgnoreCase(Long siteId, String code);
    Page<Facility> findAllBySite_Id(Long siteId, Pageable pageable);
    Page<Facility> findAllByNameIgnoreCaseContaining(String name, Pageable pageable);
}
