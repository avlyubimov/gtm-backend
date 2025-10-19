package com.gtm.gtm.point.controller;

import com.gtm.gtm.point.domain.PointType;
import com.gtm.gtm.point.dto.PointCreateDto;
import com.gtm.gtm.point.dto.PointDto;
import com.gtm.gtm.point.dto.PointUpdateDto;
import com.gtm.gtm.point.service.PointService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Points", description = "Контрольные точки")
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public PointDto create(@RequestBody @Valid PointCreateDto dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public PointDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public Page<PointDto> list(
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) PointType type,
            Pageable pageable
    ) {
        return service.list(facilityId, type, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public PointDto update(@PathVariable Long id, @RequestBody @Valid PointUpdateDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restore(@PathVariable Long id) {
        service.restore(id);
    }

    @GetMapping("/types")
    public PointType[] types() {
        return PointType.values();
    }
}
