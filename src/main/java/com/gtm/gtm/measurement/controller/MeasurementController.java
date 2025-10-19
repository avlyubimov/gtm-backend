package com.gtm.gtm.measurement.controller;

import com.gtm.gtm.measurement.domain.MeasurementType;
import com.gtm.gtm.measurement.dto.MeasurementCreateDto;
import com.gtm.gtm.measurement.dto.MeasurementDto;
import com.gtm.gtm.measurement.dto.MeasurementUpdateDto;
import com.gtm.gtm.measurement.service.MeasurementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Measurements", description = "Замеры по контрольным точкам")
@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class MeasurementController {
    private final MeasurementService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CAMERAL')")
    public Page<MeasurementDto> list(@RequestParam(required = false) Long facilityId,
                                     @RequestParam(required = false) Long pointId,
                                     @RequestParam(required = false) Long cycleId,
                                     @RequestParam(required = false) MeasurementType type,
                                     Pageable pageable) {
        return service.list(facilityId, pointId, cycleId, type, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CAMERAL')")
    public MeasurementDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CAMERAL')")
    public MeasurementDto create(@RequestBody @Valid MeasurementCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CAMERAL')")
    public MeasurementDto update(@PathVariable Long id, @RequestBody @Valid MeasurementUpdateDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
