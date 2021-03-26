package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.constant.ApiBaseEndpoints;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.model.SuccessResponse;
import ir.mab.radioamin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class UserController {

    UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/users")
    @ResponseStatus( HttpStatus.CREATED )
    SuccessResponse<User> registerUser(@Valid @RequestBody User user){
        return new SuccessResponse<User>("An Activation Code Sent To " + user.getEmail(), user);
    }
}
