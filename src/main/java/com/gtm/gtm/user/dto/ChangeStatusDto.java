package com.gtm.gtm.user.dto;

import com.gtm.gtm.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusDto(@NotNull UserStatus status) {}
