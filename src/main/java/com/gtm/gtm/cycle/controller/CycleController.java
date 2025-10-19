package com.gtm.gtm.cycle.controller;

import com.gtm.gtm.cycle.domain.CycleStatus;
import com.gtm.gtm.cycle.dto.CycleCreateDto;
import com.gtm.gtm.cycle.dto.CycleDto;
import com.gtm.gtm.cycle.dto.CycleStatusChangeDto;
import com.gtm.gtm.cycle.dto.CycleUpdateDto;
import com.gtm.gtm.cycle.service.CycleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cycles", description = "Циклы")
@RestController
@RequestMapping("/api/cycles")
@RequiredArgsConstructor
public class CycleController {
    private final CycleService service;

    @GetMapping
    public Page<CycleDto> list(@RequestParam(required = false) Long facilityId,
                               @RequestParam(required = false) CycleStatus status,
                               Pageable pageable) {
        return service.list(facilityId, status, pageable);
    }

    @GetMapping("/{id}")
    public CycleDto get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CycleDto create(@RequestBody @Valid CycleCreateDto dto) { return service.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CycleDto update(@PathVariable Long id, @RequestBody @Valid CycleUpdateDto dto) { return service.update(id, dto); }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CycleDto activate(@PathVariable Long id) { return service.activate(id); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CycleDto changeStatus(@PathVariable Long id,
                                 @RequestBody @Valid CycleStatusChangeDto dto) {
        return service.changeStatus(id, dto.status());
    }
}
