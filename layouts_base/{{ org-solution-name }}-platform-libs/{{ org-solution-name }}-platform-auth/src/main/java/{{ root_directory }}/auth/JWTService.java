package {{ root_package }}.auth;

import com.google.common.base.Strings;
import java.text.ParseException;
import java.util.*;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import {{ root_package }}.errorhandling.exceptions.TokenValidationException;

import static {{ root_package }}.auth.AuthConstants.*;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    public Optional<JWTUser> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JWTUser) {
            return Optional.of((JWTUser) authentication.getPrincipal());
        } else {
            return Optional.empty();
        }
    }

    public JWTUser decodeJWTUser(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
            //TODO: enable validation once know how get userId
            // validateToken(jwtClaimsSet);

            List<SimpleGrantedAuthority> userRoles;
            List<String> roles = jwtClaimsSet.getStringListClaim(ROLES);
            if (roles != null && !roles.isEmpty()) {
                userRoles = roles.stream()
                                 .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                 .toList();
            } else {
                userRoles = Collections.emptyList();
            }
            String issuer = jwtClaimsSet.getIssuer();
            IdentityProvider identityProvider = IdentityProviderResolver.resolveIdentityProvider(issuer);
            String subject = jwtClaimsSet.getSubject();

            Map<String, Object> metadata = jwtClaimsSet.getJSONObjectClaim(METADATA);
            String userUuid;
            //TODO review this logic
            if (metadata != null && metadata.containsKey(USER_UUID)) {
                userUuid = (String) metadata.get(USER_UUID);
            } else {
                userUuid = subject;
            }

            return new JWTUser(
                UUID.fromString(userUuid),
                identityProvider,
                subject,
                token,
                userRoles
            );

        } catch (ParseException e) {
            logger.warn("Was not able to parse token", e);
            return null;
        } catch (TokenValidationException e) {
            logger.info("Token validation failed");
            return null;
        }
    }


    //We are validating here, that token contains two our custom fields METADATA (and checking) and ROLES.
    //If token doesn't contain one or more of these fields, we throw TokenValidationException.
    //If new required custom field appears, we also should validate it here.
    private void validateToken(JWTClaimsSet claimsSet) throws ParseException {
        if (claimsSet.getClaim(METADATA) == null) {
            logger.warn("METADATA field is missing.");
            throw new TokenValidationException();
        }

        Object userUuid = claimsSet.getJSONObjectClaim(METADATA).get(USER_UUID);
        if (userUuid == null || Strings.isNullOrEmpty(userUuid.toString())) {
            logger.warn("user_uuid field is missing");
            throw new TokenValidationException();
        }

        if (claimsSet.getClaim(ROLES) == null || claimsSet.getStringListClaim(ROLES).isEmpty()) {
            logger.warn("ROLES field is missing");
            throw new TokenValidationException();
        }
    }
}
