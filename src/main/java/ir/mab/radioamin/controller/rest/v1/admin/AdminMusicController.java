package ir.mab.radioamin.controller.rest.v1.admin;

import com.sun.media.jfxmediaimpl.MediaUtils;
import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Music;
import ir.mab.radioamin.model.enums.MusicType;
import ir.mab.radioamin.model.enums.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.MusicRepository;
import ir.mab.radioamin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminMusicController {
    FileStorageService fileStorageService;
    MusicRepository musicRepository;

    @Autowired
    public AdminMusicController(FileStorageService fileStorageService, MusicRepository musicRepository) {
        this.fileStorageService = fileStorageService;
        this.musicRepository = musicRepository;
    }

    @PostMapping("/music")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Music> draftMusic(@Valid @RequestParam("music") Music music,
                                      @RequestParam(value = "cover", required = false) MultipartFile cover,
                                      @RequestParam(value = "vocal64", required = false) MultipartFile vocal64,
                                      @RequestParam(value = "vocal128", required = false) MultipartFile vocal128,
                                      @RequestParam(value = "vocal320", required = false) MultipartFile vocal320,
                                      @RequestParam(value = "video480", required = false) MultipartFile video480,
                                      @RequestParam(value = "video720", required = false) MultipartFile video720,
                                      @RequestParam(value = "video1080", required = false) MultipartFile video1080) throws HttpMediaTypeNotSupportedException {

        Music createdMusic = musicRepository.save(music);

        if (music.getMusicType() == MusicType.VOCAL) {
            if (vocal64 != null) {
                validMusicContentType(vocal64);
                fileStorageService.storeFile(StorageType.MUSIC,createdMusic.getSinger().getName(),vocal64);
            }

            if (vocal128 != null) {
                validMusicContentType(vocal128);

            }

            if (vocal320 != null) {
                validMusicContentType(vocal320);

            }
        }

        return new SuccessResponse<>("", null);

    }

    private void validMusicContentType(MultipartFile file) throws HttpMediaTypeNotSupportedException {
        if (!Arrays.asList(
                MediaUtils.CONTENT_TYPE_MP3,
                MediaUtils.CONTENT_TYPE_MPA).contains(file.getContentType())) {

            throw new HttpMediaTypeNotSupportedException(
                    MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())),
                    Arrays.asList(MediaType.parseMediaType(MediaUtils.CONTENT_TYPE_MP3),
                            MediaType.parseMediaType(MediaUtils.CONTENT_TYPE_MPA)));

        }
    }


}
