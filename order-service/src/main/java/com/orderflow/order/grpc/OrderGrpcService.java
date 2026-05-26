package com.orderflow.order.grpc;

import com.orderflow.order.service.OrderBusinessService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(OrderGrpcService.class);

    private final OrderBusinessService orderService;

    public OrderGrpcService(OrderBusinessService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void placeOrder(PlaceOrderRequest request, StreamObserver<PlaceOrderResponse> responseObserver) {
        log.info("gRPC PlaceOrder customerId={} restaurantId={}", request.getCustomerId(), request.getRestaurantId());
        PlaceOrderResponse response = orderService.placeOrder(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<Order> responseObserver) {
        log.info("gRPC GetOrder orderId={}", request.getOrderId());
        Order order = orderService.getOrder(request);
        responseObserver.onNext(order);
        responseObserver.onCompleted();
    }
}
