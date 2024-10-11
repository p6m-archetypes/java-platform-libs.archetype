package {{ root_package }}.auth.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import {{ root_package }}.auth.IdentityProvider;
import org.springframework.security.test.context.support.WithSecurityContext;
import {{ root_package }}.auth.testutils.WithJWTUserSecurityContextFactory;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithJWTUserSecurityContextFactory.class)
public @interface WithJWTUser {
    String userId() default "";

    IdentityProvider identityProvider() default IdentityProvider.AUTH0;

    String identityProviderId() default "";

    String [] roles() default {};
}
