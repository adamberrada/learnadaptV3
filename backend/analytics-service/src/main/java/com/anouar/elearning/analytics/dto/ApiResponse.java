package com.anouar.elearning.analytics.dto;

import lombok.Builder;

@Builder
public record ApiResponse<T>(boolean success, String message, T data) {}

