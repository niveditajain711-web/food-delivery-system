package com.orderflow.gateway.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlaceOrderRequestDto(
        @NotBlank String customerId,
        @NotBlank String restaurantId,
        @NotEmpty @Valid List<OrderLineDto> items
) {
}
