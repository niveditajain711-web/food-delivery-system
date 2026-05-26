package com.orderflow.gateway.dto;

public record PlaceOrderResponseDto(
        String orderId,
        String status,
        String message
) {
}
