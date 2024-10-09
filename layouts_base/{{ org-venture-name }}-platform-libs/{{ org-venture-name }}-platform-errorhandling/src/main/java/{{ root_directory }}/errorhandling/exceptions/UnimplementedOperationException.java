package {{ root_package }}.errorhandling.exceptions;

import com.google.rpc.Code;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorDetail;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.ResultPath;

import java.util.HashMap;
import java.util.Map;

/**
 * An exception that indicates improper arguments were provided in the request. This exception maps to
 * a {@link Code.UNIMPLEMENTED} status which includes the attempted operation in the message.
 */
public class UnimplementedOperationException extends {{ VentureName }}Exception {

    private final String operation;
    private UnimplementedOperationException(String operation) {
        super(formatMessage(operation));
        this.operation = operation;
    }

    public static UnimplementedOperationException from(String operation) {
        return new UnimplementedOperationException(operation);
    }

    @Override
    public Status toStatus() {
        return Status.newBuilder()
                .setCode(Code.UNIMPLEMENTED_VALUE)
                .setMessage(getDescription())
                .build();
    }

    public TypedGraphQLError toTypedGraphQLError(){
        ErrorDetail errorDetail = ErrorDetail.Common.UNIMPLEMENTED;
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("reason", Code.UNIMPLEMENTED_VALUE);

        return TypedGraphQLError.newBuilder()
                .errorType(ErrorType.UNAVAILABLE)
                .message(getDescription())
                .origin("DGS")
                .errorDetail(errorDetail)
                .debugInfo(debugInfo)
                .path(ResultPath.rootPath())
                .build();
    }
    private static String formatMessage(String operation){
        return operation+" is not implemented";
    }
}
