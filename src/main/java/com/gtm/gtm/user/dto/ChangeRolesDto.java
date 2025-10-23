package com.gtm.gtm.user.dto;


import com.gtm.gtm.user.domain.UserRole;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record ChangeRolesDto(@NotNull Set<UserRole> roles) {}
