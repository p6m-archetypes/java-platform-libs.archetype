package {{ root_package }}.errorhandling.exceptions;


import com.google.rpc.Status;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import java.util.Objects;

public abstract class {{ SolutionName }}Exception extends RuntimeException {

    private final String description;
    public abstract Status toStatus();

    public {{ SolutionName }}Exception() {
        super("No Error Description");
        this.description = "No Error Description";
    }

    public {{ SolutionName }}Exception(String description) {
        super(description);
        this.description = description;
    }

    public TypedGraphQLError toTypedGraphQLError(){
        return TypedGraphQLError.newBuilder()
                .errorType(ErrorType.UNKNOWN)
                .build();
    }

    public String getDescription() {
        return description;
    }

    public boolean isRetryable() {
        return false;
    };

}
