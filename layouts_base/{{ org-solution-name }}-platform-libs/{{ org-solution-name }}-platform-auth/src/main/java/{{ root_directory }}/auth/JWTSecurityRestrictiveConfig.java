package {{ root_package }}.auth;

import org.springframework.beans.factory.annotation.Autowired;
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
 * This Security Configuration restricts all endpoints to authenticated requests, coming in via JWT tokens.  It is not
 * possible to opt out of this requirement on a method-by-method basis.  If you need this capability, use
 * {@link JWTSecurityPermissiveConfig}, with caution.
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
public class JWTSecurityRestrictiveConfig extends WebSecurityConfigurerAdapter {
    private final JWTFilter jwtFilter;

    @Autowired
    public JWTSecurityRestrictiveConfig(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF for internal services
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/health/**", "/info/**", "/prometheus/**", "/metrics/**", "/env/**").permitAll()
                .anyRequest().authenticated()
        ;

        // No sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Intercept all calls to extract JWT
        http.addFilterBefore(jwtFilter, RequestHeaderAuthenticationFilter.class);
    }

}