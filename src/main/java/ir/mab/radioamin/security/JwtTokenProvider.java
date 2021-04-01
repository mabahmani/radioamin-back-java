package ir.mab.radioamin.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {

    public String createToken(String userIdentifier){
        try {
            return JWT.create()
                    .withSubject(userIdentifier)
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.JWT_EXPIRATION_TIME))
                    .sign(Algorithm.HMAC256(SecurityConstants.JWT_SECRET));
        } catch (JWTCreationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String verifyToken(HttpServletRequest httpServletRequest){

        String token = resolveToken(httpServletRequest);

        if (token == null){
            httpServletRequest.setAttribute(SecurityConstants.JWT_NOT_ATTACHED_ATTRIBUTE,"Access Token Not Attached.");
            throw new JWTVerificationException("Access Token Not Attached.");
        }

        if (isExpired(token)){
            httpServletRequest.setAttribute(SecurityConstants.JWT_EXPIRED_ATTRIBUTE,"Access Token Has Expired.");
            throw new JWTVerificationException("Access Token Has Expired.");
        }

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecurityConstants.JWT_SECRET))
                    .build();

            DecodedJWT jwt = verifier.verify(token);

            return jwt.getSubject();
        }
        catch (JWTVerificationException exception){
            throw new JWTVerificationException("Invalid Access Token.",exception);
        }
    }

    private boolean isExpired(String token){
        return JWT.decode(token).getExpiresAt().before(new Date());
    }

    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(SecurityConstants.JWT_HEADER_STRING);

        if(bearerToken == null){
            req.setAttribute(SecurityConstants.JWT_NOT_ATTACHED_ATTRIBUTE,"Access Token Not Attached.");
        }

        else {
            if (!bearerToken.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)){
                req.setAttribute(SecurityConstants.JWT_BEARER_NOT_ATTACHED_ATTRIBUTE,"It's not a Bearer Token.");
            }
            else {
                return bearerToken.substring(7);
            }
        }

        return null;
    }
}
