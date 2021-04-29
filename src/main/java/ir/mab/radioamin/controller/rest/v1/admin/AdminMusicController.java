package ir.mab.radioamin.controller.rest.v1.admin;

import com.sun.media.jfxmediaimpl.MediaUtils;
import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.*;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.enums.MusicType;
import ir.mab.radioamin.model.enums.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.AlbumRepository;
import ir.mab.radioamin.repository.MusicRepository;
import ir.mab.radioamin.repository.SingerRepository;
import ir.mab.radioamin.repository.UserRepository;
import ir.mab.radioamin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminMusicController {
    FileStorageService fileStorageService;
    MusicRepository musicRepository;
    SingerRepository singerRepository;
    AlbumRepository albumRepository;
    UserRepository userRepository;

    @Autowired
    public AdminMusicController(FileStorageService fileStorageService, MusicRepository musicRepository, SingerRepository singerRepository, AlbumRepository albumRepository, UserRepository userRepository) {
        this.fileStorageService = fileStorageService;
        this.musicRepository = musicRepository;
        this.singerRepository = singerRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/music")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Music> draftMusic(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("type") MusicType musicType,
            @RequestParam("singerId") Long singerId,
            @RequestParam("albumId") Long albumId,
            @RequestPart("cover") MultipartFile coverFile) throws HttpMediaTypeNotSupportedException {

        Principal principal = request.getUserPrincipal();
        User user = userRepository.findUserByEmail(principal.getName()).orElseThrow(
                ()-> new ResourceNotFoundException("user",principal.getName(),"id")
        );

        Singer singer = singerRepository.findById(singerId).orElseThrow(
                ()-> new ResourceNotFoundException("singer",String.valueOf(singerId),"id")
        );

        Album album = albumRepository.findById(albumId).orElseThrow(
                ()-> new ResourceNotFoundException("album",String.valueOf(albumId),"id")
        );

        validCoverContentType(coverFile);

        Music music = new Music();
        music.setName(name);
        music.setPublished(false);
        music.setMusicType(musicType);
        music.setUser(user);
        music.setSinger(singer);
        music.setAlbum(album);

        String url = fileStorageService.storeFile(StorageType.MUSIC_COVER,name,coverFile);
        Cover cover = new Cover();
        cover.setUrl(url);
        cover.setMusic(music);

        music.setCover(cover);

        return new SuccessResponse<>("music created",musicRepository.save(music));

    }

    private void validCoverContentType(MultipartFile file) throws HttpMediaTypeNotSupportedException {
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
