package com.yiqipin.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderPayRequest(@NotBlank String orderId, String payType) {}
