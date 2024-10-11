package {{ root_package }}.errorhandling.exceptions.graphql.errors;

import com.netflix.graphql.types.errors.ErrorDetail;
import com.netflix.graphql.types.errors.ErrorType;
import graphql.GraphQLError;

public enum {{ SolutionName }}ErrorDetail implements ErrorDetail {
  RESOURCE_ALREADY_EXISTS(ErrorType.BAD_REQUEST);

  private final ErrorType errorType;

  {{ SolutionName }}ErrorDetail(ErrorType errorType) {
    this.errorType = errorType;
  }

  @Override
  public ErrorType getErrorType() {
    return errorType;
  }

  @Override
  public Object toSpecification(GraphQLError error) {
    return errorType + "." + this;
  }
}
