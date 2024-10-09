package {{ root_package }}.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class JWTUser {
    private final UUID userId;
    private final IdentityProvider identityProvider;
    private final String identityProviderId;
    private final List<SimpleGrantedAuthority> authorities;
    private final String token;

    public JWTUser(String userId,
                   IdentityProvider identityProvider,
                   String identityProviderId,
                   String token) {
        this(UUID.fromString(userId), identityProvider, identityProviderId, token);
    }

    public JWTUser(UUID userId,
                   IdentityProvider identityProvider,
                   String identityProviderId,
                   String token) {
        this(userId, identityProvider, identityProviderId, token, new ArrayList<>());
    }

    public JWTUser(UUID userId,
                   IdentityProvider identityProvider,
                   String identityProviderId,
                   String token,
                   List<SimpleGrantedAuthority> authorities) {
        this.userId = userId;
        this.identityProvider = identityProvider;
        this.identityProviderId = identityProviderId;
        this.token = Objects.requireNonNull(token);
        this.authorities = Objects.requireNonNull(authorities);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public UUID getUserId() {
        return userId;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public String getIdentityProviderId() {
        return identityProviderId;
    }

    public String getToken() {
        return token;
    }
}
