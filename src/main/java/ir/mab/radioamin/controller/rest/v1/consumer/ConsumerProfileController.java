package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Avatar;
import ir.mab.radioamin.entity.Profile;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.ProfileRepository;
import ir.mab.radioamin.repository.UserRepository;
import ir.mab.radioamin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerProfileController {
    UserRepository userRepository;
    ProfileRepository profileRepository;
    FileStorageService fileStorageService;

    @Autowired
    public ConsumerProfileController(UserRepository userRepository, ProfileRepository profileRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/profile")
    SuccessResponse<Profile> getUserProfile() {
        User user = findUser();

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

    @PutMapping("/profile")
    SuccessResponse<Profile> updateUserProfile(@RequestBody Profile profile) {

        User user = findUser();

        Profile userProfile = user.getProfile();

        userProfile.setBio(profile.getBio());
        userProfile.setDisplayName(profile.getDisplayName());
        userProfile.setFirstName(profile.getFirstName());
        userProfile.setLastName(profile.getLastName());
        userProfile = profileRepository.save(userProfile);

        return new SuccessResponse<>("Profile Updated", userProfile);
    }

    @PutMapping(value = "/profile/avatar")
    SuccessResponse<Profile> updateUserAvatar(@RequestParam("file") MultipartFile avatarFile) throws HttpMediaTypeNotSupportedException {

        if (!Arrays.asList(
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_GIF_VALUE,
                MediaType.IMAGE_PNG_VALUE).contains(avatarFile.getContentType())){

            throw new HttpMediaTypeNotSupportedException(
                    MediaType.parseMediaType(Objects.requireNonNull(avatarFile.getContentType())),
                    Arrays.asList(MediaType.IMAGE_JPEG,
                            MediaType.IMAGE_PNG,
                            MediaType.IMAGE_GIF));

        }

        User user = findUser();
        Profile userProfile = user.getProfile();

        String downloadUrl = fileStorageService.storeFile(StorageType.USER_AVATAR, user.getEmail(), avatarFile);

        Avatar avatar = new Avatar();
        avatar.setProfile(userProfile);
        avatar.setUrl(downloadUrl);

        userProfile.setAvatar(avatar);
        userProfile = profileRepository.save(userProfile);

        return new SuccessResponse<>("Avatar Updated", userProfile);
    }

    private User findUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(email), "userId"));
    }
}
