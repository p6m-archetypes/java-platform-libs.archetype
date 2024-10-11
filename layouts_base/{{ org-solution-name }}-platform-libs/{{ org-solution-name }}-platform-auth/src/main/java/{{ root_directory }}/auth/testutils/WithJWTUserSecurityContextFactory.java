package {{ root_package }}.auth.testutils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import {{ root_package }}.auth.JWTUser;
import {{ root_package }}.auth.annotations.WithJWTUser;

public class WithJWTUserSecurityContextFactory implements WithSecurityContextFactory<WithJWTUser> {

    public static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public SecurityContext createSecurityContext(WithJWTUser withJWTUser) {
        // Create empty context
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create the jwtUser from WithJWTUser annotation
        var userId =
            StringUtils.isEmpty(withJWTUser.userId()) ? UUID.randomUUID() : UUID.fromString(withJWTUser.userId());
        var identityProvider = withJWTUser.identityProvider();
        var identityProviderId =
            StringUtils.isEmpty(withJWTUser.identityProviderId()) ? "auth0|000000000000000000000000" : withJWTUser.identityProviderId();
        var jwtToken = JWTTokenUtil.generateJwtToken(userId);
        var roles = withJWTUser.roles().length == 0 ? new String [] {DEFAULT_ROLE} : withJWTUser.roles();
        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        JWTUser jwtUser = new JWTUser(userId, identityProvider, identityProviderId, jwtToken, authorities);

        // Create Authentication Object
        Authentication auth = new PreAuthenticatedAuthenticationToken(jwtUser, jwtUser.getToken(), authorities);
        auth.setAuthenticated(true);
        // Set the Auth to the context
        context.setAuthentication(auth);

        return context;
    }
}
