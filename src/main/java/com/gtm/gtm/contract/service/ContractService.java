package com.gtm.gtm.contract.service;

import com.gtm.gtm.contract.domain.Contract;
import com.gtm.gtm.contract.dto.ContractCreateDto;
import com.gtm.gtm.contract.dto.ContractDto;
import com.gtm.gtm.contract.dto.ContractUpdateDto;
import com.gtm.gtm.common.error.ConflictException;
import com.gtm.gtm.common.error.NotFoundException;
import com.gtm.gtm.contract.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository repo;

    public Page<ContractDto> list(String customerFilter, Pageable pageable) {
        var page = (customerFilter == null || customerFilter.isBlank())
                ? repo.findAll(pageable)
                : repo.findAllByCustomerIgnoreCaseContaining(customerFilter.trim(), pageable);
        return page.map(ContractService::toDto);
    }

    public ContractDto get(Long id) {
        return repo.findById(id).map(ContractService::toDto)
                .orElseThrow(() -> new NotFoundException("Contract not found"));
    }

    @Transactional
    public ContractDto create(ContractCreateDto dto) {
        if (repo.existsByNumberIgnoreCase(dto.number()))
            throw new ConflictException("Contract number already exists");

        var c = new Contract();
        c.setNumber(dto.number().trim());
        c.setSignedAt(dto.signedAt());
        c.setCustomer(dto.customer().trim());
        c.setCustomerFullName(dto.customerFullName().trim());
        c.setCreatedAt(OffsetDateTime.now());
        c.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.save(c));
    }

    @Transactional
    public ContractDto update(Long id, ContractUpdateDto dto) {
        var c = repo.findById(id).orElseThrow(() -> new NotFoundException("Contract not found"));
        if (!c.getNumber().equalsIgnoreCase(dto.number()) && repo.existsByNumberIgnoreCase(dto.number()))
            throw new ConflictException("Contract number already exists");

        c.setNumber(dto.number().trim());
        c.setSignedAt(dto.signedAt());
        c.setCustomer(dto.customer().trim());
        c.setCustomerFullName(dto.customerFullName().trim());
        c.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.saveAndFlush(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Contract not found");
        repo.deleteById(id);
    }

    private static ContractDto toDto(Contract c) {
        return new ContractDto(
                c.getId(), c.getNumber(), c.getSignedAt(), c.getCustomer(),
                c.getCustomerFullName(), c.getCreatedAt(), c.getUpdatedAt(),
                c.getSiteCount()
        );
    }
}
