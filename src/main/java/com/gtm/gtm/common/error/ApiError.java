package com.gtm.gtm.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        Object details
) {
    public static ApiError of(int status, String error, String message, String path, Object details) {
        return new ApiError(OffsetDateTime.now().toString(), status, error, message, path, details);
    }
}
