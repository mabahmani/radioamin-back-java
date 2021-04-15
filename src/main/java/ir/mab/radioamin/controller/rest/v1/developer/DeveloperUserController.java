package ir.mab.radioamin.controller.rest.v1.developer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.RoleEnum;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.RoleRepository;
import ir.mab.radioamin.repository.SessionRepository;
import ir.mab.radioamin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.DEVELOPER)
public class DeveloperUserController {

    UserRepository userRepository;
    RoleRepository roleRepository;
    SessionRepository sessionRepository;


    @Autowired
    public DeveloperUserController(UserRepository userRepository, RoleRepository roleRepository,
                                   SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/users")
    SuccessResponse<Page<User>> findUsers(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) RoleEnum roleEnum,
            @RequestParam(value = "sort", required = false, defaultValue = "email") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        if (roleEnum != null && email != null) {
            return new SuccessResponse<>("success", userRepository.findAllByEmailContainingAndUserRolesIs(email, roleRepository.findRoleByRole(roleEnum).get(), PageRequest.of(page, size, Sort.by(direction, sort))));
        } else if (roleEnum != null) {
            return new SuccessResponse<>("success", userRepository.findAllByUserRolesIs(roleRepository.findRoleByRole(roleEnum).get(), PageRequest.of(page, size, Sort.by(direction, sort))));
        } else if (email != null) {
            return new SuccessResponse<>("success", userRepository.findAllByEmailContaining(email, PageRequest.of(page, size, Sort.by(direction, sort))));
        }
        return new SuccessResponse<>("success", userRepository.findAll(PageRequest.of(page, size, Sort.by(direction,sort))));
    }

    @GetMapping("/users/{id}")
    SuccessResponse<User> findUser(@PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("user", String.valueOf(id), "userId"));
        return new SuccessResponse<>("success", user);
    }

    @PatchMapping("/users/{id}/userRoles")
    SuccessResponse<User> setUserRoles(
            @PathVariable("id") Long id,
            @RequestBody Set<Role> roleList) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("user", String.valueOf(id), "userId"));

        user.setUserRoles(roleList);

        return new SuccessResponse<>("user roles updated", userRepository.save(user));
    }
}
