package com.orderflow.order.service;

import com.orderflow.inventory.grpc.OrderLineItem;
import com.orderflow.order.client.InventoryGrpcClient;
import com.orderflow.order.domain.OrderEntity;
import com.orderflow.order.grpc.GetOrderRequest;
import com.orderflow.order.grpc.PlaceOrderRequest;
import com.orderflow.order.grpc.PlaceOrderResponse;
import com.orderflow.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Orchestrates order placement: reserve inventory via gRPC, then persist the order.
 */
@Service
public class OrderBusinessService {

    private static final Logger log = LoggerFactory.getLogger(OrderBusinessService.class);

    private final OrderRepository orderRepository;
    private final InventoryGrpcClient inventoryClient;

    public OrderBusinessService(OrderRepository orderRepository, InventoryGrpcClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    @Transactional
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        validatePlaceOrder(request);
        String orderId = UUID.randomUUID().toString();
        List<OrderLineItem> inventoryLines = OrderMapper.toInventoryLines(request);

        inventoryClient.reserve(orderId, inventoryLines);
        try {
            orderRepository.save(OrderMapper.toEntity(orderId, request));
        } catch (RuntimeException ex) {
            log.error("Order save failed, releasing inventory orderId={}", orderId, ex);
            inventoryClient.release(orderId, inventoryLines);
            throw ex;
        }

        log.info("Order placed orderId={} customerId={}", orderId, request.getCustomerId());
        return PlaceOrderResponse.newBuilder()
                .setOrderId(orderId)
                .setStatus(com.orderflow.order.grpc.OrderStatus.CONFIRMED)
                .setMessage("Order confirmed")
                .build();
    }

    @Transactional(readOnly = true)
    public com.orderflow.order.grpc.Order getOrder(GetOrderRequest request) {
        if (request.getOrderId().isBlank()) {
            throw new IllegalArgumentException("order_id is required");
        }
        OrderEntity entity = orderRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getOrderId()));
        return OrderMapper.toProto(entity);
    }

    private void validatePlaceOrder(PlaceOrderRequest request) {
        if (request.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("customer_id is required");
        }
        if (request.getRestaurantId().isBlank()) {
            throw new IllegalArgumentException("restaurant_id is required");
        }
        if (request.getItemsCount() == 0) {
            throw new IllegalArgumentException("At least one order line is required");
        }
        for (com.orderflow.order.grpc.OrderLineItem item : request.getItemsList()) {
            if (item.getProductId().isBlank()) {
                throw new IllegalArgumentException("product_id is required on each line");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("quantity must be positive for product " + item.getProductId());
            }
        }
    }
}
