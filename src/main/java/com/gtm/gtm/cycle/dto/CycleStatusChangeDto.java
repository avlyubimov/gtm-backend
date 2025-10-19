package com.gtm.gtm.cycle.dto;

import com.gtm.gtm.cycle.domain.CycleStatus;
import jakarta.validation.constraints.NotNull;

public record CycleStatusChangeDto(@NotNull CycleStatus status) {}
