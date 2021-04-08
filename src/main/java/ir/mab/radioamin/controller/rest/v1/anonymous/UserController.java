package ir.mab.radioamin.controller.rest.v1.anonymous;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.ActivationCode;
import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.exception.TokenExpiredException;
import ir.mab.radioamin.exception.WrongCredentialsException;
import ir.mab.radioamin.model.RoleEnum;
import ir.mab.radioamin.model.req.ActivateUserRequest;
import ir.mab.radioamin.model.req.ChangeUserPasswordRequest;
import ir.mab.radioamin.model.res.JwtResponse;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.RoleRepository;
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

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

import static ir.mab.radioamin.security.SecurityConstants.ActivationCodeExpireTime;


@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ANONYMOUS)
public class UserController {

    UserRepository userRepository;
    RoleRepository roleRepository;
    EmailService emailService;
    CodeGeneratorService codeGeneratorService;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository, EmailService emailService,
                          CodeGeneratorService codeGeneratorService, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(activateUserRequest.getEmail()), "userId"));

        if (activationCodeValid(user.getActivationCode(), activateUserRequest.getActivationCode())) {
            user.setActive(true);
            User editedUser = userRepository.save(user);
            return new SuccessResponse<>("user activated.", editedUser);
        }

        throw new WrongCredentialsException("User", activateUserRequest.getActivationCode(), "ActivationCode");
    }

    @PostMapping("/users/login")
    SuccessResponse<JwtResponse> loginUser(@RequestBody User user) {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if (authentication.isAuthenticated()) {
            return new SuccessResponse<>("login successful.", new JwtResponse(jwtTokenProvider.createToken(user.getEmail())));
        }

        throw new WrongCredentialsException("User", user.getPassword(), "Password");
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
}
