package {{ root_package }}.auth;

public class IdentityProviderResolver {

    static IdentityProvider resolveIdentityProvider(String issuer) {
        if (issuer.toLowerCase().contains("auth0")) {
            return IdentityProvider.AUTH0;
        } else if (issuer.toLowerCase().contains("p6m.dev")) {
            return IdentityProvider.P_AUTH;
        } else {
            return null;
        }
    }
}
