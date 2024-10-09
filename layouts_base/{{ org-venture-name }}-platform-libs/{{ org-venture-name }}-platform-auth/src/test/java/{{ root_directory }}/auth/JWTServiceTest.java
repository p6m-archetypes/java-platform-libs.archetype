package {{ root_package }}.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import {{ root_package }}.auth.testutils.JWTTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static {{ root_package }}.auth.AuthConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JWTServiceTest {

    private JWTService jwtService;
    private static final String USER_ID_STR = "11111111-1111-1111-1111-111111111111";
    private static final PrivateKey privateKey = JWTTokenUtil.getPrivateKey();

    @BeforeEach
    void setup() {
        jwtService = new JWTService();
    }

    /*
     * Here is a code snippet for generating a new JWT token which will be needed after updating
     * the METADATA and ROLES urls. You can check the decoded token content with 'jwt.io'.
     *
     *   String token = JWTTokenUtil.generateJwtToken(UUID.fromString("21a56393-b6ef-404b-8a41-65703313e9f4"));
     *   System.out.println(token);
     */
    @Test
    void testLoadUserByToken() {
        UUID userId = UUID.randomUUID();
        var token = JWTTokenUtil.generateJwtToken(userId);
        JWTUser jwtUser = jwtService.decodeJWTUser(token);
        assertThat(jwtUser).isNotNull();
        assertThat(jwtUser.getUserId()).isEqualTo(userId);
    }

    @Test
    void when_tokenGeneratedWithMetadataAndUserUuidAndRoles_thenSuccessValidation() {
        UUID userUuid = UUID.fromString(USER_ID_STR);
        String token = JWTTokenUtil.generateJwtToken(userUuid);

        JWTUser user = jwtService.decodeJWTUser(token);
        assertEquals(
            USER_ID_STR,
            user.getUserId().toString(),
            "Field 'userUuid' in token is not equal with original UUID."
        );

        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo(UUID.fromString(USER_ID_STR));
        assertThat(user.getAuthorities()).isNotEmpty();
    }

    @Test
    void when_tokenIsEmpty_thenValidationFails() {
        String emptyToken = "";
        assertNull(jwtService.decodeJWTUser(emptyToken));
    }

    @Test
    void useSubjectAsUserIdWhenUserUuidIsNotPresent() {
        UUID userId = UUID.randomUUID();
        var token = generateTokenWithoutMetadata(userId.toString());
        JWTUser jwtUser = jwtService.decodeJWTUser(token);
        assertThat(jwtUser).isNotNull();
        assertThat(jwtUser.getUserId()).isEqualTo(userId);
    }

    //PRIVATE METHODS
    private String generateTokenWithoutMetadata() {
        return Jwts.builder()
                   .setSubject("Example")
                   .setExpiration(Date.from(Instant.ofEpochMilli(4765132800000L)))
                   .setIssuer("https://identity.p6m.dev")
                   .addClaims(generateClaimsWithoutMetadata())
                   .signWith(SignatureAlgorithm.RS256, privateKey)
                   .compact();
    }

    private String generateTokenWithoutMetadata(String subject) {
        return Jwts.builder()
                   .setSubject(subject)
                   .setExpiration(Date.from(Instant.ofEpochMilli(4765132800000L)))
                   .setIssuer("https://identity.p6m.dev")
                   .addClaims(generateClaimsWithoutMetadata())
                   .signWith(SignatureAlgorithm.RS256, privateKey)
                   .compact();
    }

    private static Map<String, Object> generateClaimsWithoutMetadata() {
        String[] roles = {"USER"};
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLES, roles);
        return claims;
    }

    private String generateTokenWithoutUserUuid() {
        return Jwts.builder()
                   .setSubject("Example")
                   .setExpiration(Date.from(Instant.ofEpochMilli(4765132800000L)))
                   .setIssuer("https://identity.p6m.dev")
                   .addClaims(generateClaimsWithoutUserUuid())
                   .signWith(SignatureAlgorithm.RS256, privateKey)
                   .compact();
    }

    private static Map<String, Object> generateClaimsWithoutUserUuid() {
        String[] roles = {"USER"};
        Map<String, Object> claims = new HashMap<>();
        claims.put(METADATA, new HashMap<>());
        claims.put(ROLES, roles);
        return claims;
    }

    private String generateTokenWithoutRoles(String userId) {
        return Jwts.builder()
                   .setSubject("Example")
                   .setExpiration(Date.from(Instant.ofEpochMilli(4765132800000L)))
                   .setIssuer("https://identity.p6m.dev")
                   .addClaims(Map.of(METADATA, Map.of(USER_UUID, userId)))
                   .signWith(SignatureAlgorithm.RS256, privateKey)
                   .compact();
    }

}
