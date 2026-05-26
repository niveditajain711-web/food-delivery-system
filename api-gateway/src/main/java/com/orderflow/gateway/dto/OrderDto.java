package com.orderflow.gateway.dto;

import java.util.List;

public record OrderDto(
        String orderId,
        String customerId,
        String restaurantId,
        String status,
        List<OrderLineDto> items
) {
}
