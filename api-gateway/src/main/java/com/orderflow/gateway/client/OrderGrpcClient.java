package com.orderflow.gateway.client;

import com.orderflow.order.grpc.GetOrderRequest;
import com.orderflow.order.grpc.Order;
import com.orderflow.order.grpc.OrderServiceGrpc;
import com.orderflow.order.grpc.PlaceOrderRequest;
import com.orderflow.order.grpc.PlaceOrderResponse;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(OrderGrpcClient.class);

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceBlockingStub orderStub;

    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        log.info("gRPC PlaceOrder customerId={}", request.getCustomerId());
        try {
            return orderStub.placeOrder(request);
        } catch (StatusRuntimeException ex) {
            log.warn("gRPC PlaceOrder failed status={}", ex.getStatus());
            throw ex;
        }
    }

    public Order getOrder(String orderId) {
        log.info("gRPC GetOrder orderId={}", orderId);
        try {
            return orderStub.getOrder(GetOrderRequest.newBuilder().setOrderId(orderId).build());
        } catch (StatusRuntimeException ex) {
            log.warn("gRPC GetOrder failed orderId={} status={}", orderId, ex.getStatus());
            throw ex;
        }
    }
}
