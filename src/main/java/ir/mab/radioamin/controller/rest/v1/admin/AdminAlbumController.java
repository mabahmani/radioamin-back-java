package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Album;
import ir.mab.radioamin.entity.Cover;
import ir.mab.radioamin.entity.Singer;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.enums.StorageType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.AlbumRepository;
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

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminAlbumController {
    FileStorageService fileStorageService;
    AlbumRepository albumRepository;
    SingerRepository singerRepository;

    @Autowired
    public AdminAlbumController(FileStorageService fileStorageService, AlbumRepository albumRepository, SingerRepository singerRepository) {
        this.fileStorageService = fileStorageService;
        this.albumRepository = albumRepository;
        this.singerRepository = singerRepository;
    }

    @GetMapping(value = "/album/count")
    SuccessResponse<Long> albumCount(){
        return new SuccessResponse<>("number of albums", albumRepository.count());
    }

    @PostMapping(value = "/album", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Album> createAlbum(
            @RequestPart("cover") MultipartFile coverFile,
            @RequestParam("name") String name,
            @RequestParam("singerId") Long singerId,
            @RequestParam(value = "releaseDate", required = false) Long releaseDate) throws HttpMediaTypeNotSupportedException {

        Singer singer = singerRepository.findById(singerId).orElseThrow(
                () -> new ResourceNotFoundException("singer", String.valueOf(singerId), "id")
        );

        validAvatarContentType(coverFile);

        String coverUrl = fileStorageService.storeFile(StorageType.ALBUM_COVER, name, coverFile);
        Cover cover = new Cover();
        cover.setUrl(coverUrl);

        Album album = new Album();
        album.setName(name);
        album.setReleaseDate(releaseDate);
        album.setSinger(singer);
        album.setCover(cover);

        return new SuccessResponse<>("album created", albumRepository.save(album));

    }



    @PutMapping(value = "/album/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    SuccessResponse<Album> updateAlbum(
            @PathVariable Long id,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "singerId") Long singerId,
            @RequestParam(value = "releaseDate", required = false) Long releaseDate) throws HttpMediaTypeNotSupportedException {

        Album album = albumRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("album", String.valueOf(id), "id")
        );

        Singer singer = singerRepository.findById(singerId).orElseThrow(
                () -> new ResourceNotFoundException("singer", String.valueOf(singerId), "id")
        );

        album.setName(name);
        album.setReleaseDate(releaseDate);
        album.setSinger(singer);

        if (coverFile != null) {
            validAvatarContentType(coverFile);
            String coverUrl = fileStorageService.storeFile(StorageType.ALBUM_COVER, album.getName(), coverFile);

            //delete old file
            fileStorageService.deleteFile(album.getCover().getFilePath());

            album.getCover().setUrl(coverUrl);
        }


        return new SuccessResponse<>("album updated", albumRepository.save(album));
    }

    @DeleteMapping("/album/{id}")
    SuccessResponse<Boolean> deleteAlbum(@PathVariable Long id) {

        Album album = albumRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("album", String.valueOf(id), "id")
        );
        albumRepository.deleteById(id);
        fileStorageService.deleteFile(album.getCover().getFilePath());
        return new SuccessResponse<>("album deleted", true);
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
