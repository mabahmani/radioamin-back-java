package ir.mab.radioamin.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import ir.mab.radioamin.service.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    AppUserDetailsService appUserDetailsService;
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtTokenFilter(AppUserDetailsService appUserDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.appUserDetailsService = appUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        try {

            String userEmail = jwtTokenProvider.verifyToken(httpServletRequest);

            UserDetails userDetails = appUserDetailsService.loadUserByUsername(userEmail);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(),userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        catch (JWTVerificationException exception){
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
