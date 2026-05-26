package com.orderflow.gateway.service;

import com.orderflow.gateway.dto.LocationUpdateDto;
import com.orderflow.gateway.dto.OrderDto;
import com.orderflow.gateway.dto.OrderLineDto;
import com.orderflow.gateway.dto.PlaceOrderRequestDto;
import com.orderflow.gateway.dto.PlaceOrderResponseDto;
import com.orderflow.order.grpc.Order;
import com.orderflow.order.grpc.OrderLineItem;
import com.orderflow.order.grpc.OrderStatus;
import com.orderflow.order.grpc.PlaceOrderRequest;
import com.orderflow.order.grpc.PlaceOrderResponse;
import com.orderflow.tracking.grpc.LocationUpdate;

import java.util.List;

public final class GrpcResponseMapper {

    private GrpcResponseMapper() {
    }

    public static PlaceOrderRequest toPlaceOrderRequest(PlaceOrderRequestDto dto) {
        PlaceOrderRequest.Builder builder = PlaceOrderRequest.newBuilder()
                .setCustomerId(dto.customerId())
                .setRestaurantId(dto.restaurantId());
        for (OrderLineDto line : dto.items()) {
            builder.addItems(OrderLineItem.newBuilder()
                    .setProductId(line.productId())
                    .setQuantity(line.quantity())
                    .build());
        }
        return builder.build();
    }

    public static PlaceOrderResponseDto toPlaceOrderResponse(PlaceOrderResponse response) {
        return new PlaceOrderResponseDto(
                response.getOrderId(),
                toStatusName(response.getStatus()),
                response.getMessage());
    }

    public static OrderDto toOrderDto(Order order) {
        List<OrderLineDto> lines = order.getItemsList().stream()
                .map(item -> new OrderLineDto(item.getProductId(), item.getQuantity()))
                .toList();
        return new OrderDto(
                order.getOrderId(),
                order.getCustomerId(),
                order.getRestaurantId(),
                toStatusName(order.getStatus()),
                lines);
    }

    public static LocationUpdateDto toLocationUpdate(LocationUpdate update) {
        return new LocationUpdateDto(
                update.getOrderId(),
                update.getLatitude(),
                update.getLongitude(),
                update.getSequence(),
                update.getDelivered());
    }

    private static String toStatusName(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "CONFIRMED";
            case OUT_FOR_DELIVERY -> "OUT_FOR_DELIVERY";
            case DELIVERED -> "DELIVERED";
            case CANCELLED -> "CANCELLED";
            case ORDER_STATUS_UNSPECIFIED, UNRECOGNIZED -> "UNKNOWN";
        };
    }
}
