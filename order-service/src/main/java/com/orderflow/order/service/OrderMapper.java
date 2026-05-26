package com.orderflow.order.service;

import com.orderflow.inventory.grpc.OrderLineItem;
import com.orderflow.order.domain.OrderEntity;
import com.orderflow.order.domain.OrderLineEntity;
import com.orderflow.order.domain.OrderStatus;
import com.orderflow.order.grpc.PlaceOrderRequest;

import java.util.List;

final class OrderMapper {

    private OrderMapper() {
    }

    static List<OrderLineItem> toInventoryLines(PlaceOrderRequest request) {
        return request.getItemsList().stream()
                .map(item -> OrderLineItem.newBuilder()
                        .setProductId(item.getProductId())
                        .setQuantity(item.getQuantity())
                        .build())
                .toList();
    }

    static com.orderflow.order.grpc.Order toProto(OrderEntity entity) {
        com.orderflow.order.grpc.Order.Builder builder = com.orderflow.order.grpc.Order.newBuilder()
                .setOrderId(entity.getOrderId())
                .setCustomerId(entity.getCustomerId())
                .setRestaurantId(entity.getRestaurantId())
                .setStatus(toProtoStatus(entity.getStatus()));
        for (OrderLineEntity line : entity.getLines()) {
            builder.addItems(com.orderflow.order.grpc.OrderLineItem.newBuilder()
                    .setProductId(line.getProductId())
                    .setQuantity(line.getQuantity())
                    .build());
        }
        return builder.build();
    }

    static OrderEntity toEntity(String orderId, PlaceOrderRequest request) {
        OrderEntity entity = new OrderEntity(
                orderId,
                request.getCustomerId(),
                request.getRestaurantId(),
                OrderStatus.CONFIRMED);
        for (com.orderflow.order.grpc.OrderLineItem item : request.getItemsList()) {
            entity.addLine(new OrderLineEntity(item.getProductId(), item.getQuantity()));
        }
        return entity;
    }

    private static com.orderflow.order.grpc.OrderStatus toProtoStatus(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> com.orderflow.order.grpc.OrderStatus.CONFIRMED;
            case OUT_FOR_DELIVERY -> com.orderflow.order.grpc.OrderStatus.OUT_FOR_DELIVERY;
            case DELIVERED -> com.orderflow.order.grpc.OrderStatus.DELIVERED;
            case CANCELLED -> com.orderflow.order.grpc.OrderStatus.CANCELLED;
        };
    }
}
