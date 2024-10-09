package {{ root_package }}.errorhandling.exceptions;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ResourceInfo;
import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorDetail;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.ResultPath;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * An exception that indicates the requested resource was not found. This exception maps to a {@link Code.NOT_FOUND}
 * status which includes the resources name and type and optionally a description and its owner in the {@link ResourceInfo}.
 */
public class ResourceNotFoundException extends {{ VentureName }}Exception {
    private final String resourceType;
    private final String resourceName;
    private final Optional<String> owner;
    private final Optional<String> description;

    public ResourceNotFoundException(
            String resourceName,
            String resourceType,
            String description,
            String owner) {
        super(formatMessage(resourceName, resourceType, description));
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.description= Optional.ofNullable(description);
        this.owner = Optional.ofNullable(owner);
    }

    public static ResourceNotFoundException from(String resourceName, String resourceType) {
        return new ResourceNotFoundException(resourceName, resourceType, null, null);
    }

    public static ResourceNotFoundException from(String resourceName, String resourceType, String description) {
        return new ResourceNotFoundException(resourceName, resourceType, description, null);
    }

    public static Supplier<ResourceNotFoundException> resourceNotFoundException(
            String resourceName,
            String resourceType
    ) {
        return () -> ResourceNotFoundException.from(resourceName, resourceType);
    }

    public static Supplier<ResourceNotFoundException> resourceNotFoundException(
            String resourceName,
            String resourceType,
            String description
    ) {
        return () -> ResourceNotFoundException.from(resourceName, resourceType, description);
    }

    @Override
    public Status toStatus() {
        var detail = ResourceInfo.newBuilder()
                .setResourceName(resourceName)
                .setResourceType(resourceType);
        description.ifPresent(detail::setDescription);
        owner.ifPresent(detail::setOwner);

        return Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage(getDescription())
                .addDetails(Any.pack(detail.build()))
                .build();
    }

    @Override
    public TypedGraphQLError toTypedGraphQLError() {
        ErrorDetail errorDetail = ErrorDetail.Common.MISSING_RESOURCE;
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("reason", Code.NOT_FOUND);

        return TypedGraphQLError.newBuilder()
                .errorType(ErrorType.NOT_FOUND)
                .message(getDescription())
                .origin("DGS")
                .errorDetail(errorDetail)
                .debugInfo(debugInfo)
                .path(ResultPath.rootPath())
                .build();
    }

    private static String formatMessage(String resourceName, String resourceType, String description) {
        var reason = "";
        if(description != null) {
            reason = String.format(" Reason: %s", description);
        }

        return String.format("Unable to find resource %s of type %s.%s", resourceName, resourceType, reason);
    }
}
