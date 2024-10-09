package {{ root_package }}.errorhandling.exceptions;

import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorDetail;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.ResultPath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * An exception that indicates improper arguments were provided in the request, analogous to {@link IllegalArgumentException}.
 * This exception maps to a {@link Code.INVALID_ARGUMENT} status which includes a map of field to violation message in
 * the {@link BadRequest}.
 */
public class InvalidArgumentException extends {{ VentureName }}Exception {
    private final Map<String, String> fieldToDescription;

    public InvalidArgumentException(Map<String, String> fieldToDescription) {
        super(formatMessage(fieldToDescription));
        this.fieldToDescription = fieldToDescription;
    }

    @Override
    public String getMessage() {
        return fieldToDescription.toString();
    }

    public static InvalidArgumentException from(String field, String description) {
        return new InvalidArgumentException(Map.of(field, description));
    }

    public static Supplier<InvalidArgumentException> invalidArgumentException(String field, String description) {
        return () -> InvalidArgumentException.from(field, description);
    }

    public Map<String, String> getViolations() {
        return fieldToDescription;
    }


    @Override
    public Status toStatus() {
        var violations = fieldToDescription.entrySet().stream()
                .map(this::toViolation)
                .collect(Collectors.toList());

        var detail = BadRequest.newBuilder()
                .addAllFieldViolations(violations)
                .build();

        return Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage(getDescription())
                .addDetails(Any.pack(detail))
                .build();
    }

    public TypedGraphQLError toTypedGraphQLError() {
        ErrorDetail errorDetail = ErrorDetail.Common.INVALID_ARGUMENT;
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("reason", Code.INVALID_ARGUMENT);


        return TypedGraphQLError.newBuilder()
                .errorType(ErrorType.BAD_REQUEST)
                .message(getDescription())
                .origin("DGS")
                .errorDetail(errorDetail)
                .debugInfo(debugInfo)
                .path(ResultPath.rootPath())
                .build();
    }

    private BadRequest.FieldViolation toViolation(Map.Entry<String, String> violationFromMap) {
        return BadRequest.FieldViolation.newBuilder()
                .setField(violationFromMap.getKey())
                .setDescription(violationFromMap.getValue())
                .build();
    }

    private static String formatMessage(Map<String, String> fieldToDescription) {
        return "Invalid argument(" + fieldToDescription + ") provided";
    }
}
