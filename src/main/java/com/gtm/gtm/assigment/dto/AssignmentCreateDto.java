package com.gtm.gtm.assigment.dto;

import jakarta.validation.constraints.NotNull;

public record AssignmentCreateDto(@NotNull Long facilityId, @NotNull Long userId) {}
