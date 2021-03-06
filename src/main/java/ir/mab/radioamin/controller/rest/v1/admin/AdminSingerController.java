package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Avatar;
import ir.mab.radioamin.entity.Singer;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.enums.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.SingerRepository;
import ir.mab.radioamin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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

    @GetMapping(value = "/singer/count")
    SuccessResponse<Long> singerCount(){
        return new SuccessResponse<>("number of singers", singerRepository.count());
    }

    @GetMapping(value = "/singer")
    SuccessResponse<Page<Singer>> findSingers(
            @RequestParam(value = "singer", required = false) String singer,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size)
    {
        if (singer != null){
            return new SuccessResponse<>("singers", singerRepository.findSingersByNameContaining(singer, PageRequest.of(page, size, direction, sort)));
        }

        return new SuccessResponse<>("singers", singerRepository.findAll(PageRequest.of(page, size, direction, sort)));

    }

    @GetMapping(value = "/singer/all")
    SuccessResponse<Iterable<Singer>> getAllSingers()
    {
        return new SuccessResponse<>("singers", singerRepository.findAll());
    }

    @PostMapping(value = "/singer", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Singer> createSinger(
            @RequestParam("name") String name,
            @RequestPart("avatar") MultipartFile avatar) throws HttpMediaTypeNotSupportedException {

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

    @PutMapping(value = "/singer/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    SuccessResponse<Singer> updateSinger(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) throws HttpMediaTypeNotSupportedException {



        Singer singer = singerRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("singer",String.valueOf(id),"id"));

        if (name != null){
            if (!singer.getName().equals(name)){
                if (singerRepository.existsSingerByName(name)){
                    throw new ResourceAlreadyExistsException("singer", name);
                }
            }
            singer.setName(name);
        }

        if (avatar != null){
            validAvatarContentType(avatar);
            String avatarUrl = fileStorageService.storeFile(StorageType.SINGER_AVATAR,singer.getName(), avatar);

            //delete old file
            fileStorageService.deleteFile(singer.getAvatar().getFilePath());

            singer.getAvatar().setUrl(avatarUrl);
        }

        return new SuccessResponse<>("singer updated", singerRepository.save(singer));
    }

    @DeleteMapping("/singer/{id}")
    SuccessResponse<Boolean> deleteSinger(@PathVariable Long id) {

        Singer singer = singerRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("singer",String.valueOf(id),"id"));

        singerRepository.deleteById(id);
        fileStorageService.deleteFile(singer.getAvatar().getFilePath());
        return new SuccessResponse<>("singer deleted", true);
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
