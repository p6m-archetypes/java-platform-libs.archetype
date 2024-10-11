package {{ root_package }}.errorhandling.exceptions.graphql.handlers;

import com.google.common.annotations.VisibleForTesting;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.ResultPath;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import {{ root_package }}.errorhandling.exceptions.graphql.util.StatusDetailUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class StatusRuntimeExceptionHandler {

    public static Optional<GraphQLError> getGraphqlErrorGivenStatusRuntimeException(StatusRuntimeException exception, ResultPath path) {
        var status = getStatusGivenStatusRuntimeException(exception);
        String serviceName = getOriginServiceName(exception.getTrailers());
        Map<String, Object> detailsMap = StatusDetailUtil.getDetails(status, exception);

        var typedGraphQLErrorBuilder = TypedGraphQLError.newBuilder()
                .message(ExceptionUtils.getMessage(exception))
                .origin(serviceName)
                .debugInfo(detailsMap)
                .path(path);

        switch (exception.getStatus().getCode()) {
            case UNAUTHENTICATED:
                return Optional.of(typedGraphQLErrorBuilder
                        .errorType(ErrorType.UNAUTHENTICATED)
                        .build());
            case PERMISSION_DENIED:
            case RESOURCE_EXHAUSTED:
                return Optional.of(typedGraphQLErrorBuilder
                        .errorType(ErrorType.PERMISSION_DENIED)
                        .build());
            case INVALID_ARGUMENT:
                return Optional.of(typedGraphQLErrorBuilder
                        .errorType(ErrorType.BAD_REQUEST)
                        .build());
            case UNIMPLEMENTED:
                return Optional.of(typedGraphQLErrorBuilder
                        .errorType(ErrorType.UNAVAILABLE)
                        .build());
            case NOT_FOUND:
                return Optional.of(typedGraphQLErrorBuilder
                        .errorType(ErrorType.NOT_FOUND)
                        .build());
            case INTERNAL:
                return Optional.of(typedGraphQLErrorBuilder
                        .errorType(ErrorType.INTERNAL)
                        .build());
            case UNKNOWN:
                return Optional.of(typedGraphQLErrorBuilder
                        .errorType(ErrorType.UNKNOWN)
                        .build());
        }
        return Optional.empty();
    }

    @VisibleForTesting
    public static Status getStatusGivenStatusRuntimeException(StatusRuntimeException e) {
        return StatusProto.fromStatusAndTrailers(e.getStatus(), e.getTrailers());
    }

    private static String getOriginServiceName(@Nullable Metadata trailer) {
        if (trailer == null) {
            return "Unknown Service";
        }
        return Optional.ofNullable(trailer.get(Metadata.Key.of(
                        "originating-service-name",
                        Metadata.ASCII_STRING_MARSHALLER)))
                .orElse("Unknown Service");
    }
}
