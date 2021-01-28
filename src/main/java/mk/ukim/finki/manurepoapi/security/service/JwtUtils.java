package mk.ukim.finki.manurepoapi.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;

import java.util.Date;

public class JwtUtils {

    private static final String SECRET = "SecretKeyToGenJWTs";
    private static final int EXPIRATION_IN_HOURS = 3;

    public static String generateToken(UserPrincipal user) {
        long currentTimeMillis = System.currentTimeMillis();
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", user.getRole())
                .withIssuedAt(new Date(currentTimeMillis))
                .withExpiresAt(new Date(currentTimeMillis + EXPIRATION_IN_HOURS * 60 * 60 * 1000))
                .sign(Algorithm.HMAC512(SECRET.getBytes()));
    }

    public static UserPrincipal extractPrincipal(String jwt) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .withClaimPresence("role")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(jwt);
            String username = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("role").asString();
            return new UserPrincipal(username, role);
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

}
