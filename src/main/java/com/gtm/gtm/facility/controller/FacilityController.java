package com.gtm.gtm.facility.controller;

import com.gtm.gtm.facility.dto.FacilityCreateDto;
import com.gtm.gtm.facility.dto.FacilityDto;
import com.gtm.gtm.facility.dto.FacilityUpdateDto;
import com.gtm.gtm.facility.dto.FacilityTreeDto;
import io.swagger.v3.oas.annotations.Operation;
import com.gtm.gtm.facility.service.FacilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Facilities", description = "Объекты")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facilities")
public class FacilityController {

    private final FacilityService service;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @GetMapping
    public Page<FacilityDto> list(@RequestParam(required = false) Long siteId,
                                  @RequestParam(required = false) String name,
                                  Pageable pageable) {
        return service.list(siteId, name, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @GetMapping("/tree")
    @Operation(summary = "Древовидный список объектов (пагинация по корневым)")
    public Page<FacilityTreeDto> listTree(@RequestParam(required = false) Long siteId,
                                          @RequestParam(required = false) String name,
                                          Pageable pageable) {
        return service.listTree(siteId, name, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @GetMapping("/{id}")
    public FacilityDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public FacilityDto create(@Valid @RequestBody FacilityCreateDto dto) {
        return service.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public FacilityDto update(@PathVariable Long id, @Valid @RequestBody FacilityUpdateDto dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
