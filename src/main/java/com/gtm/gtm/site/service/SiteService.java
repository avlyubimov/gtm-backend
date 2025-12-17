package com.gtm.gtm.site.service;

import com.gtm.gtm.contract.repository.ContractRepository;
import com.gtm.gtm.common.error.ConflictException;
import com.gtm.gtm.common.error.NotFoundException;
import com.gtm.gtm.site.domain.Site;
import com.gtm.gtm.site.dto.SiteCreateDto;
import com.gtm.gtm.site.dto.SiteDto;
import com.gtm.gtm.site.dto.SiteUpdateDto;
import com.gtm.gtm.site.repository.SiteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository repo;
    private final ContractRepository contractRepo;

    public Page<SiteDto> list(Long contractId, String nameFilter, Pageable pageable) {
        if (contractId != null) {
            return repo.findAllByContract_Id(contractId, pageable).map(SiteService::toDto);
        }
        if (nameFilter != null && !nameFilter.isBlank()) {
            return repo.findAllByNameIgnoreCaseContaining(nameFilter.trim(), pageable).map(SiteService::toDto);
        }
        return repo.findAll(pageable).map(SiteService::toDto);
    }

    public SiteDto get(Long id) {
        return repo.findById(id).map(SiteService::toDto)
                .orElseThrow(() -> new NotFoundException("Site not found"));
    }

    @Transactional
    public SiteDto create(SiteCreateDto dto) {
        var contract = contractRepo.findById(dto.contractId())
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        if (repo.existsByContract_IdAndCodeIgnoreCase(dto.contractId(), dto.code())) {
            throw new ConflictException("Site code already exists in this contract");
        }

        var s = new Site();
        s.setContract(contract);
        s.setName(dto.name().trim());
        s.setCode(dto.code().trim());
        s.setCreatedAt(OffsetDateTime.now());
        s.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.save(s));
    }

    @Transactional
    public SiteDto update(Long id, SiteUpdateDto dto) {
        var s = repo.findById(id).orElseThrow(() -> new NotFoundException("Site not found"));

        if (!s.getCode().equalsIgnoreCase(dto.code())
                && repo.existsByContract_IdAndCodeIgnoreCase(s.getContract().getId(), dto.code())) {
            throw new ConflictException("Site code already exists in this contract");
        }

        s.setName(dto.name().trim());
        s.setCode(dto.code().trim());
        s.setUpdatedAt(OffsetDateTime.now());
        return toDto(repo.saveAndFlush(s));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Site not found");
        repo.deleteById(id);
    }

    private static SiteDto toDto(Site s) {
        return new SiteDto(
                s.getId(),
                s.getContract().getId(),
                s.getName(),
                s.getCode(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                s.getFacilityCount()
        );
    }
}
