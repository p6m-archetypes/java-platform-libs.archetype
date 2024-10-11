package {{ root_package }}.errorhandling.exceptions;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ResourceInfo;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.ResultPath;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import {{ root_package }}.errorhandling.exceptions.graphql.errors.{{ SolutionName }}ErrorDetail;

public class ResourceAlreadyExistsException extends {{ SolutionName }}Exception {

  private final String resourceType;
  private final String resourceName;
  private final Optional<String> message;
  public ResourceAlreadyExistsException(
      String resourceName,
      String resourceType,
      String message
  ) {
    super(formatMessage(resourceName,resourceType));
    this.resourceName = resourceName;
    this.resourceType = resourceType;
    this.message = Optional.ofNullable(message);
  }

  public static ResourceAlreadyExistsException from(String resourceName, String resourceType) {
    return new ResourceAlreadyExistsException(resourceName, resourceType, null);
  }

  public static ResourceAlreadyExistsException from(String resourceName, String resourceType, String message) {
    return new ResourceAlreadyExistsException(resourceName, resourceType, message);
  }

  public static Supplier<ResourceAlreadyExistsException> resourceAlreadyExistsException(
      String resourceName,
      String resourceType
  ) {
    return () -> ResourceAlreadyExistsException.from(resourceName, resourceType);
  }

  public static Supplier<ResourceAlreadyExistsException> resourceAlreadyExistsException(
      String resourceName,
      String resourceType,
      String message
  ) {
    return () -> ResourceAlreadyExistsException.from(resourceName, resourceType, message);
  }

  @Override
  public Status toStatus() {
    var detail = ResourceInfo.newBuilder()
        .setResourceName(resourceName)
        .setResourceType(resourceType);
    message.ifPresent(detail::setDescription);
    return Status.newBuilder()
        .setCode(Code.ALREADY_EXISTS_VALUE)
        .setMessage(getDescription())
        .addDetails(Any.pack(detail.build()))
        .build();
  }

  @Override
  public TypedGraphQLError toTypedGraphQLError(){
    Map<String, Object> debugInfo = new HashMap<>();
    debugInfo.put("description", getDescription());
    debugInfo.put("message", getMessage());

    var errorDetail = {{ SolutionName }}ErrorDetail.RESOURCE_ALREADY_EXISTS;

    return TypedGraphQLError.newBuilder()
        .errorType(ErrorType.BAD_REQUEST)
        .message(getDescription())
        .origin("DGS")
        .errorDetail(errorDetail)
        .debugInfo(debugInfo)
        .path(ResultPath.rootPath())
        .build();
  }

  private static String formatMessage(String resourceName,String resourceType ){
    return "Resource "+resourceName+" of type "+resourceType+" is already created";
  }
}
