package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Genre;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerGenreController {

    GenreRepository genreRepository;

    @Autowired
    public ConsumerGenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping("/genre")
    SuccessResponse<Page<Genre>> getGenres(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        if (name != null){
            return new SuccessResponse<>("success", genreRepository.findGenresByNameContaining(name, PageRequest.of(page, size, Sort.by(direction, sort))));
        }


        return new SuccessResponse<>("success", genreRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sort))));

    }

    @GetMapping("/genre/{id}")
    SuccessResponse<Genre> getGenre(@PathVariable Long id) {

        Genre genre = genreRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("genre", String.valueOf(id), "id")
        );
        return new SuccessResponse<>("success", genre);

    }
}
