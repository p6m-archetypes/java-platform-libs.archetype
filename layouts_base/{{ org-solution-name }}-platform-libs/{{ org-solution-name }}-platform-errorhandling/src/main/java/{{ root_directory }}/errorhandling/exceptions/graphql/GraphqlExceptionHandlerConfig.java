package {{ root_package }}.errorhandling.exceptions.graphql;

import org.springframework.context.annotation.Bean;

public class GraphqlExceptionHandlerConfig {

    @Bean
    public GraphqlExceptionHandler graphqlExceptionHandler() {
        return new GraphqlExceptionHandler();
    }

}
