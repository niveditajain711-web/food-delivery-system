package com.orderflow.inventory.grpc;

import com.orderflow.inventory.service.InventoryBusinessService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gRPC adapter: delegates to {@link InventoryBusinessService} and maps results to protobuf.
 */
@GrpcService
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(InventoryGrpcService.class);

    private final InventoryBusinessService inventoryService;

    public InventoryGrpcService(InventoryBusinessService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void reserveItems(ReserveItemsRequest request, StreamObserver<ReserveItemsResponse> responseObserver) {
        log.info("gRPC ReserveItems orderId={}", request.getOrderId());
        inventoryService.reserve(request.getOrderId(), request.getItemsList());
        ReserveItemsResponse response = ReserveItemsResponse.newBuilder()
                .setReserved(true)
                .setMessage("Stock reserved")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void releaseItems(ReleaseItemsRequest request, StreamObserver<ReleaseItemsResponse> responseObserver) {
        log.info("gRPC ReleaseItems orderId={}", request.getOrderId());
        inventoryService.release(request.getOrderId(), request.getItemsList());
        ReleaseItemsResponse response = ReleaseItemsResponse.newBuilder()
                .setReleased(true)
                .setMessage("Stock released")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
