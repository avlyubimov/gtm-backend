package com.gtm.gtm.user.assigment.controller;

import com.gtm.gtm.common.security.CurrentUser;
import com.gtm.gtm.user.assigment.dto.AssignmentCreateDto;
import com.gtm.gtm.user.assigment.dto.AssignmentDto;
import com.gtm.gtm.user.assigment.dto.TodoPointDto;
import com.gtm.gtm.user.assigment.service.AssignmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Assignments", description = "Распределение нагрузки")
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService service;
    private final CurrentUser currentUser;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public AssignmentDto assign(@RequestBody @Valid AssignmentCreateDto dto) {
        return service.assign(dto);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/my/facilities")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CAMERAL')")
    public List<AssignmentDto> myFacilities() {
        return service.myFacilities(currentUser.id());
    }

    @GetMapping("/my/todo")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CAMERAL')")
    public List<TodoPointDto> todo(@RequestParam @NotNull Long facilityId) {
        return service.todoPoints(currentUser.id(), facilityId);
    }
}
