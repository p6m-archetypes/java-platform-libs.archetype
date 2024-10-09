package {{ root_package }}.errorhandling.exceptions;

import com.google.rpc.Code;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorDetail;
import com.netflix.graphql.types.errors.ErrorDetail.Common;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.ResultPath;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * An exception that indicates that the resource is not in an appropriate state for the requested operation,
 * analogous to {@link IllegalStateException}.
 * This exception maps to a {@link Code.FAILED_PRECONDITION}.
 */
public class InvalidStateException extends {{ VentureName }}Exception {
  private final String resourceName;
  private final String invalidState;
  private final String requestedAction;
  private final Optional<List<String>> validStates;

  public InvalidStateException(String resourceName, String invalidState,
      String requestedAction, List<String> validStates) {
    super(formatMessage(resourceName, invalidState, requestedAction, validStates));
    this.resourceName = resourceName;
    this.invalidState = invalidState;
    this.requestedAction = requestedAction;
    this.validStates = Optional.ofNullable(validStates);
  }

  public static InvalidStateException from(String resourceName, String invalidState,
      String requestedAction) {
    return new InvalidStateException(resourceName, invalidState, requestedAction, null);
  }

  public static InvalidStateException from(String resourceName, String invalidState,
      String requestedAction, List<String> validStates) {
    return new InvalidStateException(resourceName, invalidState, requestedAction, validStates);
  }

  public static Supplier<InvalidStateException> invalidStateException(String resourceName,
      String invalidState, String requestedAction) {
    return () -> InvalidStateException.from(resourceName, invalidState, requestedAction);
  }

  public static Supplier<InvalidStateException> invalidStateException(String resourceName,
      String invalidState, String requestedAction, List<String> validStates) {
    return () -> InvalidStateException.from(resourceName, invalidState, requestedAction, validStates);
  }

  @Override
  public Status toStatus() {
    return Status.newBuilder()
        .setCode(Code.FAILED_PRECONDITION_VALUE)
        .setMessage(getDescription())
        .build();
  }

  @Override
  public TypedGraphQLError toTypedGraphQLError() {
    ErrorDetail errorDetail = Common.INVALID_ARGUMENT;
    Map<String, Object> debugInfo = new HashMap<>();
    debugInfo.put("reason", Code.FAILED_PRECONDITION);

    return TypedGraphQLError.newBuilder()
        .errorType(ErrorType.FAILED_PRECONDITION)
        .message(getDescription())
        .origin("DGS")
        .errorDetail(errorDetail)
        .debugInfo(debugInfo)
        .path(ResultPath.rootPath())
        .build();
  }

  private static String formatMessage(String resourceName, String invalidState, String requestedAction,
      List<String> validStates) {
    var message = String.format("The %s is in an invalid state (%s) to perform the requested action (%s).",
        resourceName, invalidState, requestedAction);

    String requiredStatesMessage = null;
    if(validStates != null) {
      requiredStatesMessage = String.format(" Valid states: %s", validStates);
    }

    return message + ((requiredStatesMessage != null) ?
        requiredStatesMessage : "");
  }
}
