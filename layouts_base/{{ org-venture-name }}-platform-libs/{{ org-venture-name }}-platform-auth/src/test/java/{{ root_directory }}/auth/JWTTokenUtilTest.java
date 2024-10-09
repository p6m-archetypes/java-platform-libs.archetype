package {{ root_package }}.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static {{ root_package }}.auth.testutils.JWTTokenUtil.generateJwtToken;
import static {{ root_package }}.auth.testutils.JWTTokenUtil.getClaims;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JWTTokenUtilTest {
    @Test
    @DisplayName("generateJwtToken() should return valid token")
    void testGenerateJwtToken() {
        var userId = UUID.randomUUID();
        var jwt = generateJwtToken(userId);
        var jwtBody = getClaims(jwt).getBody().toString();

        assertThat(jwtBody.contains("user_uuid=" + userId)).isTrue();
        assertThat(jwtBody.contains("roles=[USER]")).isTrue(); // This should match String[] roles in JWTTokenUtil
    }
}
