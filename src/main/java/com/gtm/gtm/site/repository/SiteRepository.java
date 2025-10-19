package com.gtm.gtm.site.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.site.domain.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SiteRepository extends SoftDeleteRepository<Site, Long> {
    boolean existsByContract_IdAndCodeIgnoreCase(Long contractId, String code);
    Page<Site> findAllByContract_Id(Long contractId, Pageable pageable);
    Page<Site> findAllByNameIgnoreCaseContaining(String name, Pageable pageable);
}
