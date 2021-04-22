package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Album;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerAlbumController {

    AlbumRepository albumRepository;

    @Autowired
    public ConsumerAlbumController(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @GetMapping("/album")
    SuccessResponse<Page<Album>> getAlbums(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "singer", required = false) String singer,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {



        if (name != null && singer != null)
            return new SuccessResponse<>("success", albumRepository.findAlbumsByNameContainingAndSinger_NameContaining(name, singer, PageRequest.of(page, size, Sort.by(direction, sort))));

        else if (name != null)
            return new SuccessResponse<>("success", albumRepository.findAlbumsByNameContaining(name, PageRequest.of(page, size, Sort.by(direction, sort))));

        else if (singer != null)
            return new SuccessResponse<>("success", albumRepository.findAlbumsBySinger_NameContaining(singer, PageRequest.of(page, size, Sort.by(direction, sort))));

        return new SuccessResponse<>("success", albumRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sort))));
    }

    @GetMapping("/album/{id}")
    SuccessResponse<Album> getAlbum(@PathVariable Long id) {

        Album album = albumRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("album", String.valueOf(id), "id")
        );
        return new SuccessResponse<>("success", album);
    }
}
