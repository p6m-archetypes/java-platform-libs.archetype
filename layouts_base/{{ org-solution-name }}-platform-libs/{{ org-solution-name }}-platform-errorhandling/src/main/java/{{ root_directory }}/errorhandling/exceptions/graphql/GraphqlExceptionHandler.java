package {{ root_package }}.errorhandling.exceptions.graphql;

import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import io.grpc.StatusRuntimeException;
import {{ root_package }}.errorhandling.exceptions.{{ SolutionName }}Exception;
import {{ root_package }}.errorhandling.exceptions.UnauthorizedException;
import {{ root_package }}.errorhandling.exceptions.graphql.handlers.StatusRuntimeExceptionHandler;

import java.util.Optional;

public class GraphqlExceptionHandler implements DataFetcherExceptionHandler {
    private final DefaultDataFetcherExceptionHandler defaultHandler =
        new DefaultDataFetcherExceptionHandler();

    @Override
    public DataFetcherExceptionHandlerResult onException(
        DataFetcherExceptionHandlerParameters handlerParameters) {
        // Exception comes from gRPC
        if (handlerParameters.getException() instanceof StatusRuntimeException) {

            StatusRuntimeException statusRuntimeException =
                (StatusRuntimeException) handlerParameters.getException();
            var path = handlerParameters.getPath();

            Optional<GraphQLError> maybeGraphqlError =
                StatusRuntimeExceptionHandler.getGraphqlErrorGivenStatusRuntimeException(
                    statusRuntimeException, path);
            // check to see if successfully mapped exception
            return maybeGraphqlError
                .map(
                    graphQLError ->
                        DataFetcherExceptionHandlerResult.newResult().error(graphQLError).build())
                .orElse(defaultHandler.onException(handlerParameters));
        } else if (handlerParameters.getException() instanceof UnauthorizedException) {
            var unauthorizedException = (UnauthorizedException) handlerParameters.getException();
            return DataFetcherExceptionHandlerResult.newResult()
                                                    .error(
                                                        TypedGraphQLError.newBuilder()
                                                                         .message(unauthorizedException.getDescription())
                                                                         .origin("DGS")
                                                                         .errorType(ErrorType.PERMISSION_DENIED)
                                                                         .build())
                                                    .build();
        }
        //Added to handle any exception origination from DGS if that exception is of type {{ SolutionName }}Exception
        else if (handlerParameters.getException() instanceof {{ SolutionName }}Exception) {
            var {{ solutionName }}Exception = ({{ SolutionName }}Exception) handlerParameters.getException();
            return DataFetcherExceptionHandlerResult.newResult()
                                                    .error({{ solutionName }}Exception.toTypedGraphQLError())
                                                    .build();
        }
    else {
            // TODO: Handle Java exceptions that direct from DGS
            return defaultHandler.onException(handlerParameters);
        }
    }
}
