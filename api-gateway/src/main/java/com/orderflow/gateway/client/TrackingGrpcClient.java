package com.orderflow.gateway.client;

import com.orderflow.tracking.grpc.LocationUpdate;
import com.orderflow.tracking.grpc.StreamDeliveryLocationRequest;
import com.orderflow.tracking.grpc.TrackingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Async gRPC client for server-streaming delivery updates.
 */
@Component
public class TrackingGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(TrackingGrpcClient.class);

    @GrpcClient("tracking-service")
    private TrackingServiceGrpc.TrackingServiceStub trackingStub;

    public void streamDeliveryLocation(String orderId, Consumer<LocationUpdate> onUpdate, Runnable onComplete, Consumer<Throwable> onError) {
        log.info("gRPC StreamDeliveryLocation orderId={}", orderId);
        StreamDeliveryLocationRequest request = StreamDeliveryLocationRequest.newBuilder()
                .setOrderId(orderId)
                .build();
        trackingStub.streamDeliveryLocation(request, new StreamObserver<>() {
            @Override
            public void onNext(LocationUpdate value) {
                onUpdate.accept(value);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("gRPC stream error orderId={}", orderId, t);
                onError.accept(t);
            }

            @Override
            public void onCompleted() {
                log.info("gRPC stream completed orderId={}", orderId);
                onComplete.run();
            }
        });
    }
}
