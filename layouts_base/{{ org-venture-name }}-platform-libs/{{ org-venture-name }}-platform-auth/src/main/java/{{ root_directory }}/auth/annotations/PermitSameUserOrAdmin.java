package {{ root_package }}.auth.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or #userUuid == authentication.principal.userId.toString())")
public @interface PermitSameUserOrAdmin {

}
