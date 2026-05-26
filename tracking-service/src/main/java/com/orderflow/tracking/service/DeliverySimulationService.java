package com.orderflow.tracking.service;

import com.orderflow.tracking.grpc.LocationUpdate;
import org.springframework.stereotype.Service;

/**
 * Simulates a driver moving from a restaurant toward a customer (fixed coordinates).
 */
@Service
public class DeliverySimulationService {

    private static final int TOTAL_UPDATES = 5;
    private static final long INTERVAL_MS = 2_000;

    // Demo route: restaurant (start) -> customer (end)
    private static final double START_LAT = 12.9716;
    private static final double START_LNG = 77.5946;
    private static final double END_LAT = 12.9352;
    private static final double END_LNG = 77.6245;

    public void streamLocations(String orderId, LocationEmitter emitter) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("order_id is required");
        }
        for (int sequence = 1; sequence <= TOTAL_UPDATES; sequence++) {
            double progress = sequence / (double) TOTAL_UPDATES;
            boolean delivered = sequence == TOTAL_UPDATES;
            LocationUpdate update = LocationUpdate.newBuilder()
                    .setOrderId(orderId)
                    .setLatitude(START_LAT + (END_LAT - START_LAT) * progress)
                    .setLongitude(START_LNG + (END_LNG - START_LNG) * progress)
                    .setSequence(sequence)
                    .setDelivered(delivered)
                    .build();
            emitter.emit(update);
            if (!delivered) {
                sleep(INTERVAL_MS);
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Delivery stream interrupted", ex);
        }
    }

    @FunctionalInterface
    public interface LocationEmitter {
        void emit(LocationUpdate update);
    }
}
