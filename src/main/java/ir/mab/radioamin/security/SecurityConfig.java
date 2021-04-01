package ir.mab.radioamin.security;

import ir.mab.radioamin.exception.RestAccessDeniedHandler;
import ir.mab.radioamin.exception.RestAuthenticationEntryPoint;
import ir.mab.radioamin.service.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static ir.mab.radioamin.config.ApiBaseEndpoints.VersionOne.ANONYMOUS;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    AppUserDetailsService appUserDetailsService;
    JwtTokenFilter jwtTokenFilter;
    JwtTokenFilterConfigurer jwtTokenFilterConfigurer;
    RestAccessDeniedHandler restAccessDeniedHandler;
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;


    @Autowired
    public SecurityConfig(AppUserDetailsService appUserDetailsService,
                          JwtTokenFilter jwtTokenFilter,
                          RestAccessDeniedHandler restAccessDeniedHandler,
                          RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          JwtTokenFilterConfigurer jwtTokenFilterConfigurer
                          ) {
        this.appUserDetailsService = appUserDetailsService;
        this.jwtTokenFilter = jwtTokenFilter;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.jwtTokenFilterConfigurer = jwtTokenFilterConfigurer;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers(ANONYMOUS + "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(restAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();

        http.apply(jwtTokenFilterConfigurer);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(appUserDetailsService).passwordEncoder(encoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger-ui/**");
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return appUserDetailsService;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
