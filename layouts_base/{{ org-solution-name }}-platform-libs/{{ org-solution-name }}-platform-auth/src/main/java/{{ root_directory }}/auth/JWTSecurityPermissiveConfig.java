package {{ root_package }}.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * This Security Configuration allows all requests to any HTTP endpoint, and relies on Global Method Security annotations
 * applied by developers on classes that require projection.  This configuration should only be used for select applications
 * that require some endpoints to allow anonymous access.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        jsr250Enabled = true,
        securedEnabled = true
)
@EnableConfigurationProperties(SecurityProperties.class)
@Import({JWTSecurityComponentsConfig.class})
@Order(5)
public class JWTSecurityPermissiveConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(JWTSecurityPermissiveConfig.class);
    private final JWTFilter jwtFilter;

    @Autowired
    public JWTSecurityPermissiveConfig(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("Setting up baseline security");
        // Disable CSRF for internal services
        http.csrf().disable();

        http.authorizeRequests()
                .anyRequest().permitAll()
        ;

        // No sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Intercept all calls to extract JWT
        http.addFilterBefore(jwtFilter, RequestHeaderAuthenticationFilter.class);
    }

}