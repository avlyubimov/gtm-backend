package com.gtm.gtm.site.controller;

import com.gtm.gtm.site.dto.SiteCreateDto;
import com.gtm.gtm.site.dto.SiteDto;
import com.gtm.gtm.site.dto.SiteUpdateDto;
import com.gtm.gtm.site.service.SiteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Sites", description = "Участки")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService service;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @GetMapping
    public Page<SiteDto> list(@RequestParam(required = false) Long contractId,
                              @RequestParam(required = false) String name,
                              Pageable pageable) {
        return service.list(contractId, name, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @GetMapping("/{id}")
    public SiteDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public SiteDto create(@Valid @RequestBody SiteCreateDto dto) {
        return service.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public SiteDto update(@PathVariable Long id, @Valid @RequestBody SiteUpdateDto dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
