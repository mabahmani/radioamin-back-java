package ir.mab.radioamin.controller.rest.v1.consumer;

import com.fasterxml.jackson.annotation.JsonView;
import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Cover;
import ir.mab.radioamin.entity.Music;
import ir.mab.radioamin.entity.Playlist;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.Views;
import ir.mab.radioamin.model.enums.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.PlaylistRepository;
import ir.mab.radioamin.repository.UserRepository;
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

import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerPlaylistController {
    FileStorageService fileStorageService;
    PlaylistRepository playlistRepository;
    UserRepository userRepository;

    @Autowired
    public ConsumerPlaylistController(FileStorageService fileStorageService,PlaylistRepository playlistRepository, UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;

    }

    @GetMapping("/playlist")
    @JsonView({Views.Summary.class})
    SuccessResponse<Page<Playlist>> getPlaylists(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        if (name != null){
            return new SuccessResponse<>("success", playlistRepository.findPlaylistsByNameContaining(name, PageRequest.of(page, size, Sort.by(direction, sort))));
        }


        return new SuccessResponse<>("success", playlistRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sort))));

    }

    @GetMapping("/playlist/{id}")
    @JsonView({Views.Expand.class})
    SuccessResponse<Playlist> getPlaylist(@PathVariable Long id) {

        Playlist playlist = playlistRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("playlist", String.valueOf(id), "id")
        );
        return new SuccessResponse<>("success", playlist);

    }

    @PostMapping(value = "/playlist", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Playlist> createPlaylist(
            Principal principal,
            @RequestPart("cover") MultipartFile coverFile,
            @RequestParam("name") String name
            ) throws HttpMediaTypeNotSupportedException {

        User user = userRepository.findUserByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getName(), "userId"));



        validAvatarContentType(coverFile);

        String coverUrl = fileStorageService.storeFile(StorageType.ALBUM_COVER, name, coverFile);
        Cover cover = new Cover();
        cover.setUrl(coverUrl);


        Playlist playlist = new Playlist();
        playlist.setCover(cover);
        playlist.setCreatedAt(System.currentTimeMillis());
        playlist.setName(name);
        playlist.setUser(user);


        return new SuccessResponse<>("playlist created", playlistRepository.save(playlist));

    }

    @PutMapping(value = "/playlist/{id}")
    SuccessResponse<Playlist> updatePlaylist(
            @PathVariable Long id,
            @RequestBody Set<Music> musics
    ){

        Playlist playlist = playlistRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("playlist", String.valueOf(id), "id")
        );

        playlist.getMusics().addAll(musics);

        return new SuccessResponse<>("playlist updated", playlistRepository.save(playlist));

    }

    private void validAvatarContentType(MultipartFile file) throws HttpMediaTypeNotSupportedException {
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

}
