package {{ root_package }}.auth;

import {{ root_package }}.errorhandling.exceptions.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthorizerTest {
    @Mock
    private JWTService jwtService;
    @InjectMocks
    private UserAuthorizer userAuthorizer;

    @ParameterizedTest
    @MethodSource("isUserAuthorizerProvider")
    @DisplayName("isUserAuthorized should throw exception")
    void isUserAuthorizedThrowException(Optional<JWTUser> maybeJwtUser, String userId,
                                        String operationName) {
        when(jwtService.currentUser()).thenReturn(maybeJwtUser);
        assertThrows(UnauthorizedException.class,
            () -> userAuthorizer.isUserAuthorized(userId, operationName));
    }

    @Test
    @DisplayName("isUserAuthorized should be void")
    void isUserAuthorizedTest() {
        var userUuid = UUID.randomUUID();
        var jwtUser = new JWTUser(userUuid, IdentityProvider.AUTH0, "auth0|63fe25b43903c5cc387605cd", "JWT_TOKEN");
        var methodName = "someMethod()";

        when(jwtService.currentUser()).thenReturn(Optional.of(jwtUser));
        try {
            userAuthorizer.isUserAuthorized(userUuid.toString(), methodName);
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    private static Stream<Arguments> isUserAuthorizerProvider() {
        var userUuid = UUID.randomUUID();
        var jwtUser = new JWTUser(userUuid, IdentityProvider.AUTH0, "auth0|63fe25b43903c5cc387605cd", "JWT_TOKEN");
        var methodName = "someMethod()";
        return Stream.of(
            Arguments.of(Optional.empty(), userUuid.toString(), methodName),
            Arguments.of(Optional.of(jwtUser), UUID.randomUUID().toString(), methodName)
        );
    }
}
