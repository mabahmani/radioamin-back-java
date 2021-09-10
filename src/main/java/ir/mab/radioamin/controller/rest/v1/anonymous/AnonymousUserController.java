package ir.mab.radioamin.controller.rest.v1.anonymous;

import com.auth0.jwt.exceptions.JWTVerificationException;
import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.ActivationCode;
import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.Session;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.exception.TokenExpiredException;
import ir.mab.radioamin.exception.WrongCredentialsException;
import ir.mab.radioamin.model.enums.RoleEnum;
import ir.mab.radioamin.model.req.ActivateUserRequest;
import ir.mab.radioamin.model.req.ChangeUserPasswordRequest;
import ir.mab.radioamin.model.res.JwtResponse;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.RoleRepository;
import ir.mab.radioamin.repository.SessionRepository;
import ir.mab.radioamin.repository.UserRepository;
import ir.mab.radioamin.security.JwtTokenProvider;
import ir.mab.radioamin.service.CodeGeneratorService;
import ir.mab.radioamin.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

import static ir.mab.radioamin.RadioaminApplication.generatePassayPassword;
import static ir.mab.radioamin.security.SecurityConstants.*;


@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ANONYMOUS)
public class AnonymousUserController {

    UserRepository userRepository;
    RoleRepository roleRepository;
    SessionRepository sessionRepository;
    EmailService emailService;
    CodeGeneratorService codeGeneratorService;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AnonymousUserController(UserRepository userRepository, RoleRepository roleRepository,
                                   SessionRepository sessionRepository,
                                   EmailService emailService,
                                   CodeGeneratorService codeGeneratorService, PasswordEncoder passwordEncoder,
                                   AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
        this.codeGeneratorService = codeGeneratorService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<User> registerUser(@Valid @RequestBody User user) {

        Optional<User> internalUser = userRepository.findUserByEmail(user.getEmail());

        if (internalUser.isPresent()) {
            throw new ResourceAlreadyExistsException("Email", user.getEmail());
        } else {
            String code = codeGeneratorService.generateActivationCode();

            ActivationCode activationCode = new ActivationCode();
            activationCode.setCode(code);
            activationCode.setCreatedTime(System.currentTimeMillis());
            activationCode.setUser(user);

            user.setActive(false);
            user.setCreatedAt(System.currentTimeMillis());
            user.setActivationCode(activationCode);
            user.setPassword(passwordEncoder.encode(user.getPassword()));


            Role consumerRole = roleRepository.findRoleByRole(RoleEnum.CONSUMER)
                    .orElseGet(() -> {
                        Role role = new Role(RoleEnum.CONSUMER);
                        return roleRepository.save(role);
                    });

            user.setUserRoles(Collections.singleton(consumerRole));

            userRepository.save(user);

            emailService.sendActivationCode(code);
        }

        return new SuccessResponse<>("An Activation Code Sent To " + user.getEmail(), user);
    }

    @PatchMapping("/users/active")
    SuccessResponse<User> activateUser(@RequestBody ActivateUserRequest activateUserRequest) {

        User user = userRepository.findUserByEmail(activateUserRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", activateUserRequest.getEmail(), "userId"));

        if (activationCodeValid(user.getActivationCode(), activateUserRequest.getActivationCode())) {
            user.setActive(true);
            User editedUser = userRepository.save(user);
            return new SuccessResponse<>("user activated.", editedUser);
        }

        throw new WrongCredentialsException("User", activateUserRequest.getActivationCode(), "ActivationCode");
    }

    @PostMapping("/users/login")
    SuccessResponse<JwtResponse> loginUser(
            @RequestHeader("User-Agent") String userAgent,
            @RequestBody User user,
            HttpServletRequest httpServletRequest
    ) {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if (authentication.isAuthenticated()) {

            String accessToken = jwtTokenProvider.createToken(user.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            Session session = new Session();
            session.setIp(httpServletRequest.getRemoteAddr());
            session.setLastUpdate(System.currentTimeMillis());
            session.setRefreshToken(refreshToken);
            session.setUserAgent(userAgent);
            session.setUser(
                    userRepository.findUserByEmail(user.getEmail())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("User", user.getEmail(), "userId")));

            sessionRepository.save(session);

            return new SuccessResponse<>("login successful",
                    new JwtResponse(accessToken, refreshToken));
        }

        throw new WrongCredentialsException("User", user.getPassword(), "Password");
    }

    @PostMapping("/users/login-google")
    SuccessResponse<JwtResponse> loginUserByGoogle(
            @RequestHeader("User-Agent") String userAgent,
            @RequestBody String googleTokenId,
            HttpServletRequest httpServletRequest
    ) {

        String userEmail = jwtTokenProvider.verifyGoogleTokenIdAndGetEmail(googleTokenId.replace("\"",""));

        if (userEmail != null) {
            String password = generatePassayPassword();
            User user = userRepository.findUserByEmail(userEmail)
                    .orElseGet(() -> {
                                User newUser = new User();
                                newUser.setEmail(userEmail);
                                newUser.setActive(true);
                                newUser.setCreatedAt(System.currentTimeMillis());
                                Role consumerRole = roleRepository.findRoleByRole(RoleEnum.CONSUMER)
                                        .orElseGet(() -> {
                                            Role role = new Role(RoleEnum.CONSUMER);
                                            return roleRepository.save(role);
                                        });

                                newUser.setUserRoles(Collections.singleton(consumerRole));

                                return userRepository.save(newUser);
                            }
                    );

            user.setPassword(passwordEncoder.encode(password));

            Authentication authentication =
                    authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), password));

            if (authentication.isAuthenticated()) {

                String accessToken = jwtTokenProvider.createToken(user.getEmail());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

                Session session = new Session();
                session.setIp(httpServletRequest.getRemoteAddr());
                session.setLastUpdate(System.currentTimeMillis());
                session.setRefreshToken(refreshToken);
                session.setUserAgent(userAgent);
                session.setUser(
                        userRepository.findUserByEmail(user.getEmail())
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("User", user.getEmail(), "userId")));

                sessionRepository.save(session);

                return new SuccessResponse<>("login successful",
                        new JwtResponse(accessToken, refreshToken));
            }

        }

        throw new WrongCredentialsException("User", googleTokenId, "GoogleTokenId");

    }

    @PostMapping("/users/new-token")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<JwtResponse> newToken(
            @RequestHeader("User-Agent") String userAgent,
            @RequestHeader(JWT_HEADER_STRING) String refreshToken,
            HttpServletRequest httpServletRequest) {

        if (!refreshToken.startsWith(JWT_TOKEN_PREFIX)) {
            throw new JWTVerificationException("It's not a Bearer token");
        }

        refreshToken = refreshToken.substring(7);

        Session session = sessionRepository.findSessionByRefreshToken(refreshToken)
                .orElseThrow(() -> new JWTVerificationException("Invalid Session"));

        if (jwtTokenProvider.refreshTokenIsValid(refreshToken)) {

            String email = jwtTokenProvider.getUserIdentifier(refreshToken);
            String newAccessToken = jwtTokenProvider.createToken(email);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

            session.setUserAgent(userAgent);
            session.setRefreshToken(newRefreshToken);
            session.setLastUpdate(System.currentTimeMillis());
            session.setIp(httpServletRequest.getRemoteAddr());
            sessionRepository.save(session);

            return new SuccessResponse<>("new accessToken generated",
                    new JwtResponse(newAccessToken, newRefreshToken));
        }

        throw new JWTVerificationException("Invalid refreshToken");
    }

    @PostMapping("/users/activation-code")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Boolean> sendActivationCode(@RequestBody String email) {

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(email), "userId"));

        String code = codeGeneratorService.generateActivationCode();

        ActivationCode activationCode = new ActivationCode();
        activationCode.setCode(code);
        activationCode.setCreatedTime(System.currentTimeMillis());
        activationCode.setUser(user);

        user.setActivationCode(activationCode);

        userRepository.save(user);

        emailService.sendActivationCode(code);

        return new SuccessResponse<>("An Activation Code Sent To " + user.getEmail(), true);
    }

    @PatchMapping("/users/password")
    SuccessResponse<User> changeUserPassword(@Valid @RequestBody ChangeUserPasswordRequest changeUserPasswordRequest) {

        User user = userRepository.findUserByEmail(changeUserPasswordRequest.getUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(changeUserPasswordRequest.getUser().getEmail()), "userId"));

        if (activationCodeValid(user.getActivationCode(), changeUserPasswordRequest.getActivationCode())) {
            user.setPassword(passwordEncoder.encode(changeUserPasswordRequest.getUser().getPassword()));
            User editedUser = userRepository.save(user);
            return new SuccessResponse<>("Password Changed", editedUser);
        }

        throw new WrongCredentialsException("User", changeUserPasswordRequest.getActivationCode(), "ActivationCode");
    }

    @PostMapping("/users/logout")
    SuccessResponse<Boolean> userLogout(@RequestHeader(JWT_HEADER_STRING) String refreshToken) {

        refreshToken = resolveRefreshToken(refreshToken);

        Session session = sessionRepository.findSessionByRefreshToken(refreshToken)
                .orElseThrow(() -> new JWTVerificationException("Invalid Session"));

        if (jwtTokenProvider.refreshTokenIsValid(refreshToken)) {

            sessionRepository.delete(session);

            return new SuccessResponse<>("user logged out successfully",
                    true);
        }

        throw new JWTVerificationException("Invalid refreshToken");
    }


    private Boolean activationCodeValid(ActivationCode userInDbActivationCode, String requestActivationCode) {
        if (userInDbActivationCode.getCode().equals(requestActivationCode)) {
            if (System.currentTimeMillis() - userInDbActivationCode.getCreatedTime() < ActivationCodeExpireTime) {
                return true;
            } else {
                throw new TokenExpiredException("ActivationCode");
            }
        }

        return false;
    }

    private String resolveRefreshToken(String refreshToken) {
        if (!refreshToken.startsWith(JWT_TOKEN_PREFIX)) {
            throw new JWTVerificationException("It's not a Bearer token");
        }
        return refreshToken.substring(7);
    }
}
