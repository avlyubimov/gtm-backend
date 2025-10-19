package com.gtm.gtm.user.assigment.dto;

import jakarta.validation.constraints.NotNull;

public record AssignmentCreateDto(@NotNull Long facilityId, @NotNull Long userId) {}
