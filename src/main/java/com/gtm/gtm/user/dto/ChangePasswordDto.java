package com.gtm.gtm.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank @Size(min = 8, message = "Password must be at least 8 chars")
        String newPassword
) {
}
