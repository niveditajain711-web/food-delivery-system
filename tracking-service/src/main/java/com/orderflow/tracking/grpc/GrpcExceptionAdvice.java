package com.orderflow.tracking.grpc;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public Status handleIllegalArgument(IllegalArgumentException exception) {
        return Status.INVALID_ARGUMENT.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler(IllegalStateException.class)
    public Status handleIllegalState(IllegalStateException exception) {
        return Status.INTERNAL.withDescription(exception.getMessage());
    }
}
