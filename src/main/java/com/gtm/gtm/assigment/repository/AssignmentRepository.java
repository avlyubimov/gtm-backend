package com.gtm.gtm.assigment.repository;

import com.gtm.gtm.assigment.domain.Assignment;
import com.gtm.gtm.common.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AssignmentRepository extends SoftDeleteRepository<Assignment, Long> {
    boolean existsBySite_IdAndActiveTrue(Long siteId);

    Page<Assignment> findAllByUser_IdAndActiveTrue(Long userId, Pageable pageable);

    Optional<Assignment> findByUser_IdAndSite_IdAndActiveTrue(Long userId, Long siteId);
}
