package com.orderflow.gateway.dto;

public record LocationUpdateDto(
        String orderId,
        double latitude,
        double longitude,
        int sequence,
        boolean delivered
) {
}
