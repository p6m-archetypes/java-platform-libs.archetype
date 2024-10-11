package {{ root_package }}.errorhandling.exceptions;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.DebugInfo;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorDetail;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.ResultPath;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * A fatal internal service exception that should be thrown in cases where the request should not be retried.
 * This exception maps to a {@link Code.INTERNAL} status which includes an exception's message and stacktrace in
 * the {@link DebugInfo}.
 */
public class FatalServiceException extends {{ SolutionName }}Exception {

    private final String message;
    private final List<String> stackTrace;
    private FatalServiceException(Throwable cause) {
        super(formatMessage(cause.getMessage()));
        this.message = cause.getMessage();
        this.stackTrace = Arrays.stream(cause.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(toList());
    }

    public static FatalServiceException from(Throwable e) {
        return new FatalServiceException(e);
    }

    @Override
    public Status toStatus() {
        var detail = DebugInfo.newBuilder()
                .setDetail(message)
                .addAllStackEntries(stackTrace)
                .build();

        return Status.newBuilder()
                .setCode(Code.INTERNAL_VALUE)
                .setMessage(getDescription())
                .addDetails(Any.pack(detail))
                .build();
    }

    public TypedGraphQLError toTypedGraphQLError(){
        ErrorDetail errorDetail = ErrorDetail.Common.SERVICE_ERROR;
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("reason", Code.INTERNAL_VALUE);

        return TypedGraphQLError.newBuilder()
                .errorType(ErrorType.INTERNAL)
                .message(getDescription())
                .origin("DGS")
                .errorDetail(errorDetail)
                .debugInfo(debugInfo)
                .path(ResultPath.rootPath())
                .build();
    }

    private static String formatMessage(String message){
        return "Fatal internal service exception occurred with message::"+message;
    }
}
