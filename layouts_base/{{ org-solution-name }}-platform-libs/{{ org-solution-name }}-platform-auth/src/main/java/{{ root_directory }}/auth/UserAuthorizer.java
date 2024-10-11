package {{ root_package }}.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import {{ root_package }}.errorhandling.exceptions.UnauthorizedException;

@Component
public class UserAuthorizer {
  private static final Logger logger = LoggerFactory.getLogger(UserAuthorizer.class);
  private final JWTService jwtService;

  public UserAuthorizer(JWTService jwtService) {this.jwtService = jwtService;}

  public void isUserAuthorized(String userId, String operationName) {
    var maybeJWTUser = jwtService.currentUser();
    if (maybeJWTUser.isEmpty() || maybeJWTUser.get().getUserId() == null) {
      logger.error("NOT_AUTHORIZED: Unknown user performed: " + operationName);
      throw UnauthorizedException.from(operationName);
    }

    var jwtUserId = maybeJWTUser.get().getUserId();
    if (!jwtUserId.toString().equals(userId)) {
      logger.error(
          "NOT_AUTHORIZED: UserId: " + jwtUserId + " is not authorized for " + operationName);
      throw UnauthorizedException.from(operationName);
    }
  }
}
