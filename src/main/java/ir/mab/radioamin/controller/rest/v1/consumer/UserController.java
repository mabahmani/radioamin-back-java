package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.constant.ApiBaseEndpoints;
import ir.mab.radioamin.entity.ActivationCode;
import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceExpiredException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.exception.WrongCredentialsException;
import ir.mab.radioamin.model.RoleEnum;
import ir.mab.radioamin.model.SuccessResponse;
import ir.mab.radioamin.repository.RoleRepository;
import ir.mab.radioamin.repository.UserRepository;
import ir.mab.radioamin.service.CodeGeneratorService;
import ir.mab.radioamin.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class UserController {

    UserRepository userRepository;
    RoleRepository roleRepository;
    EmailService emailService;
    CodeGeneratorService codeGeneratorService;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository, EmailService emailService, CodeGeneratorService codeGeneratorService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.codeGeneratorService = codeGeneratorService;
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
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));


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

    @PutMapping("/users/{id}/active")
    SuccessResponse<User> activateUser(@RequestBody String activationCode, @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(id), "userId"));

        if (user.getActivationCode().getCode().equals(activationCode)) {
            if (System.currentTimeMillis() - user.getActivationCode().getCreatedTime() < 5 * 60 * 1000) {
                user.setActive(true);
                User editedUser = userRepository.save(user);
                return new SuccessResponse<>("user activated.", editedUser);
            } else {
                throw new ResourceExpiredException("ActivationCode");
            }
        } else {
            throw new WrongCredentialsException("User", activationCode, "ActivationCode");
        }
    }
}
