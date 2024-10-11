package {{ root_package }}.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides an importable Spring configuration for components required by JWT authorization security configurations.
 */
@Configuration
public class JWTSecurityComponentsConfig {

    @Bean
    public JWTFilter jwtFilter(JWTService jwtService) {
        return new JWTFilter(jwtService);
    }

    @Bean
    public JWTService jwtService() {
        return new JWTService();
    }

    @Bean
    public UserAuthorizer userAuthorizer(JWTService jwtService) {
        return new UserAuthorizer(jwtService);
    }

}
