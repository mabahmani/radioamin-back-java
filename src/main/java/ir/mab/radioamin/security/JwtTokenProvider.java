package ir.mab.radioamin.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;

import static ir.mab.radioamin.security.SecurityConstants.*;

@Component
public class JwtTokenProvider {

    public String createToken(String userIdentifier) {
        try {
            return JWT.create()
                    .withSubject(userIdentifier)
                    .withIssuer(JWT_ISSUER)
                    .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                    .sign(Algorithm.HMAC256(JWT_SECRET));
        } catch (JWTCreationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String createRefreshToken(String userIdentifier) {
        try {
            return JWT.create()
                    .withSubject(userIdentifier)
                    .withIssuer(REFRESH_TOKEN_ISSUER)
                    .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                    .sign(Algorithm.HMAC256(REFRESH_TOKEN_SECRET));
        } catch (JWTCreationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Boolean refreshTokenIsValid(String token) {

        if (token == null)
            return false;

        if (isExpired(token))
            return false;

        if (!isRefreshToken(token))
            return false;

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(REFRESH_TOKEN_SECRET))
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public String verifyToken(HttpServletRequest httpServletRequest) {

        String token = resolveToken(httpServletRequest);

        if (token == null) {
            httpServletRequest.setAttribute(JWT_NOT_ATTACHED_ATTRIBUTE, "Access Token Not Attached.");
            throw new JWTVerificationException("Access Token Not Attached.");
        }

        if (isExpired(token)) {
            httpServletRequest.setAttribute(JWT_EXPIRED_ATTRIBUTE, "Access Token Has Expired.");
            throw new JWTVerificationException("Access Token Has Expired.");
        }

        if (!isAccessToken(token)) {
            throw new JWTVerificationException("Invalid Access Token.");
        }

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .build();

            DecodedJWT jwt = verifier.verify(token);

            return jwt.getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Invalid Access Token.", exception);
        }
    }

    public String verifyGoogleTokenIdAndGetEmail(String token) {


        DecodedJWT jwt;
        try {
            jwt = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider(new URL("https://www.googleapis.com/oauth2/v3/certs"));
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null).verify(jwt);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new JWTVerificationException("Invalid Access Token.");
        }

        String iss = jwt.getIssuer();
        Date exp = jwt.getExpiresAt();
        String azp = String.valueOf(jwt.getClaim("azp")).replaceAll("\"", "");
        List<String> audList = jwt.getAudience();

        if (iss != null && !iss.equals(SecurityConstants.GOOGLE_ISSUER)) {
            throw new JWTVerificationException("Invalid Access Token. (iss)");
        }

        if (!azp.equals(GOOGLE_AZP)) {
            throw new JWTVerificationException("Invalid Access Token. (azp)");
        }

        if (audList != null && !audList.contains(GOOGLE_AUD)) {
            throw new JWTVerificationException("Invalid Access Token. (aud)");
        }

        if (exp != null && exp.getTime() < System.currentTimeMillis()) {
            throw new JWTVerificationException("Access Token Has Expired. (exp)");
        }

        return String.valueOf(jwt.getClaim("email")).replaceAll("\"", "");
    }

    public String getUserIdentifier(String token) {
        return JWT.decode(token).getSubject();
    }

    private boolean isAccessToken(String token) {
        return JWT.decode(token).getIssuer().equals(JWT_ISSUER);
    }


    private boolean isRefreshToken(String token) {
        return JWT.decode(token).getIssuer().equals(REFRESH_TOKEN_ISSUER);
    }

    private boolean isExpired(String token) {
        return JWT.decode(token).getExpiresAt().before(new Date());
    }

    public long getExpiredAt(String token) {
        return JWT.decode(token).getExpiresAt().getTime();
    }

    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(JWT_HEADER_STRING);

        if (bearerToken == null) {
            req.setAttribute(JWT_NOT_ATTACHED_ATTRIBUTE, "Access Token Not Attached.");
        } else {
            if (!bearerToken.startsWith(JWT_TOKEN_PREFIX)) {
                req.setAttribute(JWT_BEARER_NOT_ATTACHED_ATTRIBUTE, "It's not a Bearer Token.");
            } else {
                return bearerToken.substring(7);
            }
        }

        return null;
    }
}
