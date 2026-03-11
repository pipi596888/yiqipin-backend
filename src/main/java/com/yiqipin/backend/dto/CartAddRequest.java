package com.yiqipin.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartAddRequest(@NotNull Long productId, @Min(1) int quantity) {}
