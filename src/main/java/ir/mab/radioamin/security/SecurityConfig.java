package ir.mab.radioamin.security;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.exception.RestAccessDeniedHandler;
import ir.mab.radioamin.exception.RestAuthenticationEntryPoint;
import ir.mab.radioamin.model.enums.RoleEnum;
import ir.mab.radioamin.service.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static ir.mab.radioamin.config.ApiBaseEndpoints.VersionOne.*;

@EnableWebSecurity
public class SecurityConfig {


    @Configuration
    @Order(1)
    public static class RestConfigurerAdapter extends WebSecurityConfigurerAdapter{
        AppUserDetailsService appUserDetailsService;
        JwtTokenFilter jwtTokenFilter;
        JwtTokenFilterConfigurer jwtTokenFilterConfigurer;
        RestAccessDeniedHandler restAccessDeniedHandler;
        RestAuthenticationEntryPoint restAuthenticationEntryPoint;


        @Autowired
        public RestConfigurerAdapter(AppUserDetailsService appUserDetailsService,
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

            http.antMatcher(ApiBaseEndpoints.BASE + "/**").authorizeRequests()
                    .antMatchers(ANONYMOUS + "/**").permitAll()
                    .antMatchers(CONSUMER + "/**").hasAuthority(RoleEnum.CONSUMER.name())
                    .antMatchers(ADMIN + "/**").hasAuthority(RoleEnum.ADMIN.name())
                    .antMatchers(DEVELOPER + "/**").hasAuthority(RoleEnum.DEVELOPER.name())
                    .anyRequest().authenticated()
                    .and()
                    .exceptionHandling()
                    .defaultAccessDeniedHandlerFor(restAccessDeniedHandler, new AntPathRequestMatcher(ApiBaseEndpoints.BASE + "/**"))
                    .defaultAuthenticationEntryPointFor(restAuthenticationEntryPoint, new AntPathRequestMatcher(ApiBaseEndpoints.BASE + "/**"))
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
        protected UserDetailsService userDetailsService() {
            return appUserDetailsService;
        }

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Configuration
    @Order(2)
    public static class WebConfigurerAdapter extends WebSecurityConfigurerAdapter{
        AppUserDetailsService appUserDetailsService;

        @Autowired
        public WebConfigurerAdapter(AppUserDetailsService appUserDetailsService) {
            this.appUserDetailsService = appUserDetailsService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.authorizeRequests()
                .antMatchers("/swagger-ui/**","/v2/api-docs/**").hasAuthority(RoleEnum.DEVELOPER.name())
                    .anyRequest().authenticated()
                    .and()
                    .formLogin();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(appUserDetailsService).passwordEncoder(encoder());
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().antMatchers(
                    "/uploads/**",
                    "/configuration/ui",
                    "/swagger-resources/**",
                    "/configuration/security",
                    "/webjars/**");
        }

        @Override
        protected UserDetailsService userDetailsService() {
            return appUserDetailsService;
        }
    }

    @Bean
    public static PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<JwtTokenFilter> jwtTokenFilterRegistration(JwtTokenFilter filter) {
        //@Component in JwtTokenFilter cause spring boot automatically set filter to spring security
        //by this code we can disable it
        FilterRegistrationBean<JwtTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
