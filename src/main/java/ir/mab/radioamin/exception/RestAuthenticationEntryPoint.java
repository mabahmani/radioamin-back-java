package ir.mab.radioamin.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.mab.radioamin.config.ErrorEndpoints;
import ir.mab.radioamin.model.Error;
import ir.mab.radioamin.model.ErrorResponse;
import ir.mab.radioamin.model.ErrorType;
import ir.mab.radioamin.security.SecurityConstants;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        ErrorResponse response;
        String accessTokenNotAttached = (String) httpServletRequest.getAttribute(SecurityConstants.JWT_NOT_ATTACHED_ATTRIBUTE);
        String notBearerToken = (String) httpServletRequest.getAttribute(SecurityConstants.JWT_BEARER_NOT_ATTACHED_ATTRIBUTE);
        String expired = (String) httpServletRequest.getAttribute(SecurityConstants.JWT_EXPIRED_ATTRIBUTE);

        if (notBearerToken != null) {
            response = new ErrorResponse(new Error(ErrorType.Unauthorized, "AccessToken",
                    notBearerToken,
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.Unauthorized).toUriString()));
        } else if (accessTokenNotAttached != null) {
            response = new ErrorResponse(new Error(ErrorType.Unauthorized, "AccessToken",
                    accessTokenNotAttached,
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.Unauthorized).toUriString()));
        } else if (expired != null) {
            response = new ErrorResponse(new Error(ErrorType.Unauthorized, "AccessToken",
                    expired,
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.Unauthorized).toUriString()));
        } else {
            response = new ErrorResponse(new Error(ErrorType.Unauthorized, "AccessToken",
                    "Invalid AccessToken",
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.Unauthorized).toUriString()));
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType("application/json");
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }
}