package com.orderflow.order.client;

import com.orderflow.inventory.grpc.InventoryServiceGrpc;
import com.orderflow.inventory.grpc.OrderLineItem;
import com.orderflow.inventory.grpc.ReleaseItemsRequest;
import com.orderflow.inventory.grpc.ReserveItemsRequest;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * gRPC client wrapper for inventory reserve/release with logging and error propagation.
 */
@Component
public class InventoryGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryGrpcClient.class);

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;

    public void reserve(String orderId, List<OrderLineItem> items) {
        log.info("Calling inventory ReserveItems orderId={}", orderId);
        ReserveItemsRequest request = ReserveItemsRequest.newBuilder()
                .setOrderId(orderId)
                .addAllItems(items)
                .build();
        try {
            inventoryStub.reserveItems(request);
        } catch (StatusRuntimeException ex) {
            log.warn("Inventory reserve failed orderId={} status={}", orderId, ex.getStatus());
            throw ex;
        }
    }

    public void release(String orderId, List<OrderLineItem> items) {
        log.info("Calling inventory ReleaseItems orderId={}", orderId);
        ReleaseItemsRequest request = ReleaseItemsRequest.newBuilder()
                .setOrderId(orderId)
                .addAllItems(items)
                .build();
        try {
            inventoryStub.releaseItems(request);
        } catch (StatusRuntimeException ex) {
            log.warn("Inventory release failed orderId={} status={}", orderId, ex.getStatus());
            throw ex;
        }
    }
}
