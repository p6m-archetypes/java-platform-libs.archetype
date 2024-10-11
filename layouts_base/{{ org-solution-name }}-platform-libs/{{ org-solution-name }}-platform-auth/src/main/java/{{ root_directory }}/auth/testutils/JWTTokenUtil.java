package {{ root_package }}.auth.testutils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static {{ root_package }}.auth.AuthConstants.*;

public class JWTTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTTokenUtil.class);

    private static final PublicKey publicKey;
    private static final PrivateKey privateKey;
    private static final String[] roles = {"USER"};

    static {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Can't get instance for KeyPairGenerator.", e);
        }
        assert keyPairGenerator != null;
        keyPairGenerator.initialize(2048);

        KeyPair kp = keyPairGenerator.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    public static String generateJwtToken(UUID userId) {
        return Jwts.builder()
                   .setSubject("Example")
                   .setExpiration(Date.from(Instant.ofEpochMilli(4765132800000L)))
                   .setIssuer("https://identity.p6m.dev/")
                   .addClaims(generateClaims(userId.toString()))
                   .signWith(SignatureAlgorithm.RS256, privateKey)
                   .compact();
    }

    public static Jws<Claims> getClaims(String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    public static String getPublicKey() {
        return "-----BEGIN PUBLIC KEY-----\n" + publicKey + "\n-----END PUBLIC KEY-----";
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }


    //PRIVATE METHODS
    private static Map<String, Object> generateClaims(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(METADATA, Map.of(USER_UUID, userId));
        claims.put(ROLES, roles);
        return claims;
    }

}
