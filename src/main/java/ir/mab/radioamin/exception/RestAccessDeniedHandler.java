package ir.mab.radioamin.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.mab.radioamin.config.ErrorEndpoints;
import ir.mab.radioamin.model.Error;
import ir.mab.radioamin.model.ErrorResponse;
import ir.mab.radioamin.model.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ErrorResponse response = new ErrorResponse(new Error(ErrorType.AccessDenied,"","Access Denied.",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.AccessDenied).toUriString()));
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        httpServletResponse.setContentType("application/json");
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }
}