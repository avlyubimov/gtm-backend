package com.gtm.gtm.contract.controller;

import com.gtm.gtm.contract.dto.ContractCreateDto;
import com.gtm.gtm.contract.dto.ContractDto;
import com.gtm.gtm.contract.dto.ContractUpdateDto;
import com.gtm.gtm.contract.service.ContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Contracts", description = "Договора")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService service;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ContractDto create(@Valid @RequestBody ContractCreateDto dto) {
        return service.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @GetMapping
    public Page<ContractDto> list(@RequestParam(required = false) String customer,
                                  Pageable pageable) {
        return service.list(customer, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @GetMapping("/{id}")
    public ContractDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ContractDto update(@PathVariable Long id,
                              @Valid @RequestBody ContractUpdateDto dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
