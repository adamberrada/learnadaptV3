package com.anouar.elearning.ai.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        boolean success,
        String message,
        String path,
        Map<String, String> errors
) {
}
