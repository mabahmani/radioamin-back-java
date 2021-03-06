package ir.mab.radioamin.controller.rest.v1.admin;

import com.sun.media.jfxmediaimpl.MediaUtils;
import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.*;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.enums.MusicType;
import ir.mab.radioamin.model.enums.MusicUrlType;
import ir.mab.radioamin.model.enums.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.*;
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

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminMusicController {
    FileStorageService fileStorageService;
    MusicRepository musicRepository;
    SingerRepository singerRepository;
    AlbumRepository albumRepository;
    UserRepository userRepository;
    LanguageRepository languageRepository;
    MusicUrlRepository musicUrlRepository;

    @Autowired
    public AdminMusicController(MusicUrlRepository musicUrlRepository,FileStorageService fileStorageService, MusicRepository musicRepository, SingerRepository singerRepository, AlbumRepository albumRepository, UserRepository userRepository, LanguageRepository languageRepository) {
        this.fileStorageService = fileStorageService;
        this.musicRepository = musicRepository;
        this.singerRepository = singerRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;
        this.musicUrlRepository = musicUrlRepository;
    }

    @GetMapping(value = "/music/count")
    SuccessResponse<Long> musicCount(){
        return new SuccessResponse<>("number of musics", musicRepository.count());
    }

    @GetMapping(value = "/music")
    SuccessResponse<Page<Music>> findMusics(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {
        if (name != null) {
            return new SuccessResponse<>("musics", musicRepository.findMusicByNameContaining(name, PageRequest.of(page, size, direction, sort)));
        }

        return new SuccessResponse<>("musics", musicRepository.findAll(PageRequest.of(page, size, direction, sort)));
    }

    @PostMapping("/music")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Music> draftMusic(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("type") MusicType musicType,
            @RequestParam("singerId") Long singerId,
            @RequestParam("albumId") Long albumId,
            @RequestParam("languageId") Long languageId,
            @RequestPart("cover") MultipartFile coverFile) throws HttpMediaTypeNotSupportedException {

        Principal principal = request.getUserPrincipal();
        User user = userRepository.findUserByEmail(principal.getName()).orElseThrow(
                () -> new ResourceNotFoundException("user", principal.getName(), "id")
        );

        Singer singer = singerRepository.findById(singerId).orElseThrow(
                () -> new ResourceNotFoundException("singer", String.valueOf(singerId), "id")
        );

        Album album = albumRepository.findById(albumId).orElseThrow(
                () -> new ResourceNotFoundException("album", String.valueOf(albumId), "id")
        );

        Language language = languageRepository.findById(languageId).orElseThrow(
                () -> new ResourceNotFoundException("language",String.valueOf(languageId),"id")
        );

        validCoverContentType(coverFile);

        Music music = new Music();
        music.setName(name);
        music.setPublished(false);
        music.setMusicType(musicType);
        music.setUser(user);
        music.setSinger(singer);
        music.setAlbum(album);
        music.setLanguage(language);

        String url = fileStorageService.storeFile(StorageType.MUSIC_COVER, name, coverFile);
        Cover cover = new Cover();
        cover.setUrl(url);
        cover.setMusic(music);

        music.setCover(cover);

        return new SuccessResponse<>("music created", musicRepository.save(music));

    }

    @PostMapping("/music/upload/{id}")
    SuccessResponse<Set<MusicUrl>> uploadMusic(
            @PathVariable("id") Long musicId,
            @RequestParam("musicUrlType") MusicUrlType musicUrlType,
            @RequestPart("music") MultipartFile musicFile) throws HttpMediaTypeNotSupportedException {

        Music music = musicRepository.findById(musicId).orElseThrow(
                () -> new ResourceNotFoundException("music", String.valueOf(musicId), "id"));

        if (music.getMusicType() == MusicType.VOCAL) {
            validMusicContentType(musicFile);
        } else {
            validVideoContentType(musicFile);
        }

        Optional<MusicUrl> foundedMusicUrl = music.getMusicUrls().stream().filter(musicUrl -> musicUrl.getMusicUrlType() == musicUrlType).findFirst();

        if (foundedMusicUrl.isPresent()) {
            fileStorageService.deleteFile(foundedMusicUrl.get().getFilePath());
            music.getMusicUrls().remove(foundedMusicUrl.get());
            musicUrlRepository.delete(foundedMusicUrl.get());
        }


        String url = fileStorageService.storeFile(StorageType.MUSIC, music.getName(), musicFile);
        MusicUrl musicUrl = new MusicUrl();
        musicUrl.setMusic(music);
        musicUrl.setUrl(url);
        musicUrl.setMusicUrlType(musicUrlType);

        music.getMusicUrls().add(musicUrl);

        musicRepository.save(music);

        return new SuccessResponse<>("music uploaded", music.getMusicUrls());

    }

    @PutMapping("/music/genre/{id}")
    SuccessResponse<Music> addMusicGenres(
            @PathVariable("id") Long musicId,
            @RequestBody Set<Genre> genres){

        Music music = musicRepository.findById(musicId).orElseThrow(
                () -> new ResourceNotFoundException("music", String.valueOf(musicId), "id"));

        music.setGenres(genres);

        return new SuccessResponse<>("music genres updated", musicRepository.save(music));

    }

    @PutMapping("/music/publish/{id}")
    SuccessResponse<Music> changeMusicPublishState(
            @PathVariable("id") Long musicId){

        Music music = musicRepository.findById(musicId).orElseThrow(
                () -> new ResourceNotFoundException("music", String.valueOf(musicId), "id"));

        music.setPublished(!music.getPublished());

        return new SuccessResponse<>("music publish state updated", musicRepository.save(music));

    }

    @PutMapping("/music/lyric/{id}")
    SuccessResponse<Music> addMusicLyric(
            @PathVariable("id") Long musicId,
            @RequestParam String lyric){

        Music music = musicRepository.findById(musicId).orElseThrow(
                () -> new ResourceNotFoundException("music", String.valueOf(musicId), "id"));

        music.setLyric(lyric);

        return new SuccessResponse<>("music lyric updated", musicRepository.save(music));

    }

    @DeleteMapping("/music/deleteMusic/{id}")
    SuccessResponse<Boolean> deleteMusicFile(
            @PathVariable("id") Long musicId,
            @RequestParam("musicUrlType") MusicUrlType musicUrlType){

        Music music = musicRepository.findById(musicId).orElseThrow(
                () -> new ResourceNotFoundException("music", String.valueOf(musicId), "id"));


        Optional<MusicUrl> foundedMusicUrl = music.getMusicUrls().stream().filter(musicUrl -> musicUrl.getMusicUrlType() == musicUrlType).findFirst();

        if (foundedMusicUrl.isPresent()) {
            fileStorageService.deleteFile(foundedMusicUrl.get().getFilePath());
            music.getMusicUrls().remove(foundedMusicUrl.get());
            musicUrlRepository.delete(foundedMusicUrl.get());
        }

        musicRepository.save(music);
        return new SuccessResponse<>("music file deleted",true);

    }

    @DeleteMapping("/music/{id}")
    SuccessResponse<Boolean> deleteMusic(
            @PathVariable("id") Long musicId) {

        Music music = musicRepository.findById(musicId).orElseThrow(
                () -> new ResourceNotFoundException("music", String.valueOf(musicId), "id"));

        musicRepository.deleteById(musicId);

        for (MusicUrl musicUrl : music.getMusicUrls()){
            fileStorageService.deleteFile(musicUrl.getFilePath());
        }

        fileStorageService.deleteFile(music.getCover().getFilePath());


        return new SuccessResponse<>("music deleted", true);
    }

    private void validCoverContentType(MultipartFile file) throws HttpMediaTypeNotSupportedException {
        if (!Arrays.asList(
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_GIF_VALUE,
                MediaType.IMAGE_PNG_VALUE).contains(file.getContentType())) {

            throw new HttpMediaTypeNotSupportedException(
                    MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())),
                    Arrays.asList(MediaType.IMAGE_JPEG,
                            MediaType.IMAGE_PNG,
                            MediaType.IMAGE_GIF));

        }
    }

    private void validMusicContentType(MultipartFile file) throws HttpMediaTypeNotSupportedException {
        if (!Objects.equals(MediaUtils.CONTENT_TYPE_MPA, file.getContentType())) {

            throw new HttpMediaTypeNotSupportedException(
                    MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())),
                    Collections.singletonList(MediaType.parseMediaType(MediaUtils.CONTENT_TYPE_MPA)));

        }
    }

    private void validVideoContentType(MultipartFile file) throws HttpMediaTypeNotSupportedException {
        if (!Arrays.asList(
                MediaUtils.CONTENT_TYPE_MP4,
                MediaUtils.CONTENT_TYPE_FLV,
                MediaUtils.CONTENT_TYPE_M4V).contains(file.getContentType())) {

            throw new HttpMediaTypeNotSupportedException(
                    MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())),
                    Arrays.asList(MediaType.parseMediaType(MediaUtils.CONTENT_TYPE_MP4),
                            MediaType.parseMediaType(MediaUtils.CONTENT_TYPE_FLV),
                            MediaType.parseMediaType(MediaUtils.CONTENT_TYPE_M4V)));

        }
    }


}
