package com.orderflow.gateway.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderflow.gateway.client.TrackingGrpcClient;
import com.orderflow.gateway.dto.LocationUpdateDto;
import com.orderflow.gateway.service.GrpcResponseMapper;
import com.orderflow.tracking.grpc.LocationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Exposes gRPC server streaming as Server-Sent Events for the React UI.
 */
@RestController
@RequestMapping("/api/orders")
public class TrackingController {

    private static final Logger log = LoggerFactory.getLogger(TrackingController.class);
    private static final long SSE_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(90);

    private final TrackingGrpcClient trackingClient;
    private final ObjectMapper objectMapper;

    public TrackingController(TrackingGrpcClient trackingClient, ObjectMapper objectMapper) {
        this.trackingClient = trackingClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/{orderId}/track", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter trackDelivery(@PathVariable String orderId) {
        log.info("REST track stream orderId={}", orderId);
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        trackingClient.streamDeliveryLocation(
                orderId,
                update -> sendUpdate(emitter, update),
                emitter::complete,
                error -> emitter.completeWithError(error));

        emitter.onTimeout(emitter::complete);
        return emitter;
    }

    private void sendUpdate(SseEmitter emitter, LocationUpdate update) {
        try {
            LocationUpdateDto dto = GrpcResponseMapper.toLocationUpdate(update);
            emitter.send(SseEmitter.event()
                    .name("location")
                    .data(objectMapper.writeValueAsString(dto)));
        } catch (IOException ex) {
            emitter.completeWithError(ex);
        }
    }
}
