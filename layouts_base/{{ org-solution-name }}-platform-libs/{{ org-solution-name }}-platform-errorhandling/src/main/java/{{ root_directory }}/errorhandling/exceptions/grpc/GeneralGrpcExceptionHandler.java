package {{ root_package }}.errorhandling.exceptions.grpc;

import io.grpc.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import {{ root_package }}.errorhandling.exceptions.grpc.handlers.ExceptionHandler;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Objects;
import java.util.Optional;

@GRpcGlobalInterceptor
public final class GeneralGrpcExceptionHandler implements ServerInterceptor {
    final Logger log = LoggerFactory.getLogger(GeneralGrpcExceptionHandler.class);

    private static final String serviceName = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("Unknown");
    private static final Metadata.Key<String> serviceKey = Metadata.Key.of("originating-service-name", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        try {
            ServerCall.Listener<ReqT> delegate = serverCallHandler.startCall(serverCall, metadata);
            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {
                @Override
                public void onReady() {
                    try {
                        super.onReady();
                    } catch (Exception e) {
                        handleError(e, serverCall);
                    }
                }

                @Override
                public void onMessage(ReqT message) {
                    try {
                        super.onMessage(message);
                    } catch (Exception e) {
                        handleError(e, serverCall);
                    }
                }

                @Override
                public void onHalfClose() {
                    try {
                        super.onHalfClose();
                    } catch (Exception e) {
                        handleError(e, serverCall);
                    }
                }
            };
        } catch (Exception e) {
            return handleInterceptorError(e, serverCall);
        }
    }

    private <ReqT, RespT> void handleError(Exception e, ServerCall<ReqT, RespT> serverCall) {
        logExceptionOut(e);

        var statusRuntimeException = ExceptionHandler.getStatusRuntimeException(e);
        var trailers = statusRuntimeException.getTrailers();
        Objects.requireNonNullElseGet(trailers, Metadata::new).put(serviceKey, serviceName);

        serverCall.close(statusRuntimeException.getStatus(), trailers);
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> handleInterceptorError(Exception e, ServerCall<ReqT, RespT> serverCall) {
        logExceptionOut(e);

        handleError(e, serverCall);
        return new ServerCall.Listener<>() {
        };
    }

    private void logExceptionOut(Exception e) {
        MDC.put("exception.name", e.getClass().getName());
        MDC.put("exception.canonicalName", e.getClass().getCanonicalName());
        MDC.put("exception.packageName", e.getClass().getPackageName());
        MDC.put("exception.stackTrace", getStackTrace(e));
        if (e.getCause() != null) {
            MDC.put("exception.cause.name", e.getCause().getClass().getName());
            MDC.put("exception.cause.canonicalName", e.getCause().getClass().getCanonicalName());
            MDC.put("exception.cause.packageName", e.getCause().getClass().getPackageName());
            MDC.put("exception.cause.stackTrace", getStackTrace(e.getCause()));
            log.error("Exception occurred in gRPC request: {} caused by {}", e.getMessage(), e.getCause().getMessage());
        } else {
            log.error("Exception occurred in gRPC request: {}", e.getMessage());
        }
    }

    private String getStackTrace(Throwable t) {
        var writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
