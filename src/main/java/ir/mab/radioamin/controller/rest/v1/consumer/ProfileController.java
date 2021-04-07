package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Profile;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.ProfileRepository;
import ir.mab.radioamin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ProfileController {
    UserRepository userRepository;
    ProfileRepository profileRepository;

    @Autowired
    public ProfileController(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @GetMapping("/profile")
    SuccessResponse<Profile> getUserProfile() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(email), "userId"));

        Profile profile = user.getProfile();

        if (profile == null) {
            System.out.println("profile null");
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
            user = userRepository.save(user);
        }

        return new SuccessResponse<>("Success", user.getProfile());
    }
}
