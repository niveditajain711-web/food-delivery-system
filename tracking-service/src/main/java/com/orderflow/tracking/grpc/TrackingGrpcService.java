package com.orderflow.tracking.grpc;

import com.orderflow.tracking.service.DeliverySimulationService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-streaming RPC: emits one {@link LocationUpdate} every few seconds until delivered.
 */
@GrpcService
public class TrackingGrpcService extends TrackingServiceGrpc.TrackingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(TrackingGrpcService.class);

    private final DeliverySimulationService simulationService;

    public TrackingGrpcService(DeliverySimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    public void streamDeliveryLocation(
            StreamDeliveryLocationRequest request,
            StreamObserver<LocationUpdate> responseObserver) {

        String orderId = request.getOrderId();
        log.info("gRPC StreamDeliveryLocation started orderId={}", orderId);

        simulationService.streamLocations(orderId, update -> {
            log.debug("Emitting location orderId={} sequence={}", orderId, update.getSequence());
            responseObserver.onNext(update);
        });
        responseObserver.onCompleted();
        log.info("gRPC StreamDeliveryLocation completed orderId={}", orderId);
    }
}
