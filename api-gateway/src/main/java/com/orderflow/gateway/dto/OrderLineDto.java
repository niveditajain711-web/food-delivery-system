package com.orderflow.gateway.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderLineDto(
        @NotBlank String productId,
        @Min(1) int quantity
) {
}
