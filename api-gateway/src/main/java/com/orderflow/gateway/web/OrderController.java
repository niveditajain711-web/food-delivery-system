package com.orderflow.gateway.web;

import com.orderflow.gateway.client.OrderGrpcClient;
import com.orderflow.gateway.dto.OrderDto;
import com.orderflow.gateway.dto.PlaceOrderRequestDto;
import com.orderflow.gateway.dto.PlaceOrderResponseDto;
import com.orderflow.gateway.service.GrpcResponseMapper;
import com.orderflow.order.grpc.PlaceOrderRequest;
import com.orderflow.order.grpc.PlaceOrderResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderGrpcClient orderClient;

    public OrderController(OrderGrpcClient orderClient) {
        this.orderClient = orderClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceOrderResponseDto placeOrder(@Valid @RequestBody PlaceOrderRequestDto request) {
        log.info("REST PlaceOrder customerId={} restaurantId={}", request.customerId(), request.restaurantId());
        PlaceOrderRequest grpcRequest = GrpcResponseMapper.toPlaceOrderRequest(request);
        PlaceOrderResponse grpcResponse = orderClient.placeOrder(grpcRequest);
        return GrpcResponseMapper.toPlaceOrderResponse(grpcResponse);
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrder(@PathVariable String orderId) {
        log.info("REST GetOrder orderId={}", orderId);
        return GrpcResponseMapper.toOrderDto(orderClient.getOrder(orderId));
    }
}
