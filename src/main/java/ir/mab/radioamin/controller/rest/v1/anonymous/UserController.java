package ir.mab.radioamin.controller.rest.v1.anonymous;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.ActivationCode;
import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.exception.TokenExpiredException;
import ir.mab.radioamin.exception.WrongCredentialsException;
import ir.mab.radioamin.model.JwtResponse;
import ir.mab.radioamin.model.RoleEnum;
import ir.mab.radioamin.model.SuccessResponse;
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

import static ir.mab.radioamin.config.GeneralConstants.ActivationCodeExpireTime;

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

    @PatchMapping("/users/{id}/active")
    SuccessResponse<User> activateUser(@RequestBody String activationCode, @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(id), "userId"));

        if (user.getActivationCode().getCode().equals(activationCode)) {
            if (System.currentTimeMillis() - user.getActivationCode().getCreatedTime() < ActivationCodeExpireTime) {
                user.setActive(true);
                User editedUser = userRepository.save(user);
                return new SuccessResponse<>("user activated.", editedUser);
            } else {
                throw new TokenExpiredException("ActivationCode");
            }
        } else {
            throw new WrongCredentialsException("User", activationCode, "ActivationCode");
        }
    }

    @PostMapping("/users/login")
    SuccessResponse<JwtResponse> loginUser(@RequestBody User user) {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if (authentication.isAuthenticated()){
            return new SuccessResponse<>("login successful.",new JwtResponse(jwtTokenProvider.createToken(user.getEmail())));
        }

        throw new WrongCredentialsException("User",user.getPassword(),"Password");
    }
}