package com.orderflow.order.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public Status handleIllegalArgument(IllegalArgumentException exception) {
        return Status.INVALID_ARGUMENT.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler(StatusRuntimeException.class)
    public Status handleStatusRuntime(StatusRuntimeException exception) {
        return exception.getStatus();
    }
}
