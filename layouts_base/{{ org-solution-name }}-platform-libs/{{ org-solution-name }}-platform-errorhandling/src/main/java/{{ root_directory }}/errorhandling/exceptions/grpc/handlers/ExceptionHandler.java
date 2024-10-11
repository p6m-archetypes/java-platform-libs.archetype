package {{ root_package }}.errorhandling.exceptions.grpc.handlers;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Any;
import com.google.rpc.*;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import {{ root_package }}.errorhandling.exceptions.{{ SolutionName }}Exception;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionHandler {
    private static final int LIMIT_TRACE_CHARACTERS = 200;
    private static final int TRACE_CHARACTERS = 100;

    public static StatusRuntimeException getStatusRuntimeException(Exception e) {
        if (e instanceof StatusRuntimeException) {
            return (StatusRuntimeException) e;
        }
        var status = getGoogleErrorStatus(e);
        return StatusProto.toStatusRuntimeException(status);
    }

    private static Status getGoogleErrorStatus(Exception e) {
        if (e instanceof {{ SolutionName }}Exception) {
            return (({{ SolutionName }}Exception) e).toStatus();
        } else if (e instanceof IllegalArgumentException) {
            return getIllegalArgumentStatus((IllegalArgumentException) e);
        } else if (e instanceof UnsupportedOperationException) {
            return getUnsupportedOperationStatus((UnsupportedOperationException) e);
        } else {
            return getUnknownStatus(e);
        }
    }

    private static Status getIllegalArgumentStatus(IllegalArgumentException e) {
        var violation = BadRequest.FieldViolation.newBuilder()
                .setField("exceptionMessage")
                .setDescription(safelyGetMessage(e))
                .build();

        var detail = BadRequest.newBuilder()
                .addFieldViolations(violation)
                .build();

        return Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage("Invalid Arguments Provided: " + e.getMessage())
                .addDetails(Any.pack(detail))
                .build();
    }

    private static Status getUnsupportedOperationStatus(UnsupportedOperationException e) {
        var detail = ResourceInfo.newBuilder()
                .setResourceName("Not Specified")
                .setResourceType("Endpoint")
                .setOwner("{{ SolutionName }}")
                .setDescription(safelyGetMessage(e))
                .build();

        return Status.newBuilder()
                .setCode(Code.UNIMPLEMENTED_VALUE)
                .setMessage("Unimplemented Endpoint: " + e.getMessage())
                .addDetails(Any.pack(detail))
                .build();
    }

    private static Status getUnknownStatus(Exception e) {
        var detail = ErrorInfo.newBuilder()
                .setReason("Unknown Error")
                .setDomain("Domain not Specified")
                .build();

        return Status.newBuilder()
                .setCode(Code.UNKNOWN_VALUE)
                .setMessage("An unknown error occurred: " + e.getMessage())
                .addDetails(0, Any.pack(detail))
                .build();
    }

    @VisibleForTesting
    public static String safelyGetMessage(Throwable e) {
        return ExceptionUtils.getMessage(e);
    }

    @VisibleForTesting
    public static String safelyGetStackTraceString(Throwable e) {

        return cutTrace(ExceptionUtils.getStackTrace(e));
    }

    private static String cutTrace(String trace) {
        return trace.length() > LIMIT_TRACE_CHARACTERS ?
                trace.substring(0,TRACE_CHARACTERS)
                        .concat(trace.substring(trace.length() -
                                (TRACE_CHARACTERS+1), trace.length() - 1)) : trace;
    }
}
