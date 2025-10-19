package com.gtm.gtm.contract.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.contract.domain.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ContractRepository extends SoftDeleteRepository<Contract, Long> {
    boolean existsByNumberIgnoreCase(String number);
    Page<Contract> findAllByCustomerIgnoreCaseContaining(String customer, Pageable pageable);
}
