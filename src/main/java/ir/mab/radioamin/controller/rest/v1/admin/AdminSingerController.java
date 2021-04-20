package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Avatar;
import ir.mab.radioamin.entity.Singer;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.model.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.SingerRepository;
import ir.mab.radioamin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminSingerController {
    FileStorageService fileStorageService;
    SingerRepository singerRepository;

    @Autowired
    public AdminSingerController(FileStorageService fileStorageService, SingerRepository singerRepository) {
        this.fileStorageService = fileStorageService;
        this.singerRepository = singerRepository;
    }

    @PostMapping("/singer")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Singer> createSinger(
            @RequestParam("name") String name,
            @RequestParam("avatar") MultipartFile avatar) throws HttpMediaTypeNotSupportedException {

        if (singerRepository.existsSingerByName(name)){
            throw new ResourceAlreadyExistsException("singer", name);
        }

        validAvatarContentType(avatar);

        String avatarUrl = fileStorageService.storeFile(StorageType.SINGER_AVATAR,name, avatar);

        Avatar createdAvatar = new Avatar();
        createdAvatar.setUrl(avatarUrl);

        Singer singer = new Singer();
        singer.setName(name);
        singer.setAvatar(createdAvatar);


        return new SuccessResponse<>("singer created", singerRepository.save(singer));

    }

    private void validAvatarContentType(MultipartFile file) throws HttpMediaTypeNotSupportedException {
        if (!Arrays.asList(
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_GIF_VALUE,
                MediaType.IMAGE_PNG_VALUE).contains(file.getContentType())){

            throw new HttpMediaTypeNotSupportedException(
                    MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())),
                    Arrays.asList(MediaType.IMAGE_JPEG,
                            MediaType.IMAGE_PNG,
                            MediaType.IMAGE_GIF));

        }
    }


}
