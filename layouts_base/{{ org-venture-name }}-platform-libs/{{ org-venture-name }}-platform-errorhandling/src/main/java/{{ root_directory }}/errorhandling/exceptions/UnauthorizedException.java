package {{ root_package }}.errorhandling.exceptions;

import com.google.rpc.Code;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.ResultPath;

import javax.security.sasl.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

/**
 * An exception that indicates improper authorization of the user provided in request authorization
 * header, analogous to {@link AuthenticationException}. This exception maps to a {@link
 * Code.PERMISSION_DENIED} status.
 */
public class UnauthorizedException extends {{ VentureName }}Exception {
  private final String operation;
  private UnauthorizedException(String operation) {
    super(formatMessage(operation));
    this.operation = operation;
  }

  public static UnauthorizedException from(String operation) {
    return new UnauthorizedException(operation);
  }

  @Override
  public Status toStatus() {
    return Status.newBuilder()
        .setCode(Code.PERMISSION_DENIED_VALUE)
        .setMessage(getDescription())
        .build();
  }

  public TypedGraphQLError toTypedGraphQLError(){

    Map<String, Object> debugInfo = new HashMap<>();
    debugInfo.put("reason", Code.PERMISSION_DENIED);

    return TypedGraphQLError.newBuilder()
            .errorType(ErrorType.PERMISSION_DENIED)
            .message(getDescription())
            .origin("DGS")
            .debugInfo(debugInfo)
            .path(ResultPath.rootPath())
            .build();
  }

  private static String formatMessage(String operation){
    return String.format("User is not authorized for %s", operation);
  }
}
