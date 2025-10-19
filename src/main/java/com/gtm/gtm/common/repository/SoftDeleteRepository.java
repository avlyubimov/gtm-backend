package com.gtm.gtm.common.repository;

import com.gtm.gtm.common.domain.SoftDeletable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteRepository<T extends SoftDeletable, ID> extends JpaRepository<T, ID> {
    void softDeleteById(ID id);
    void restoreById(ID id);
}
