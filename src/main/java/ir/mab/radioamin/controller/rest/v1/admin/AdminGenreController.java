package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Genre;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminGenreController {
    GenreRepository genreRepository;

    @Autowired
    public AdminGenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping(value = "/genre/count")
    SuccessResponse<Long> genreCount(){
        return new SuccessResponse<>("number of genres", genreRepository.count());
    }

    @GetMapping(value = "/genre")
    SuccessResponse<Page<Genre>> findGenres(
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {
        return new SuccessResponse<>("languages", genreRepository.findAll(PageRequest.of(page, size, direction, sort)));
    }

    @GetMapping(value = "/genre/all")
    SuccessResponse<Iterable<Genre>> getAllGenres() {
        return new SuccessResponse<>("languages", genreRepository.findAll());
    }

    @PostMapping("/genre")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Genre> createGenre(@RequestParam("name") String name) {

        if (genreRepository.existsGenreByName(name)){
            throw new ResourceAlreadyExistsException("genre", name);
        }

        Genre genre = new Genre();
        genre.setName(name);

        return new SuccessResponse<>("genre created", genreRepository.save(genre));

    }

    @PutMapping("/genre/{id}")
    SuccessResponse<Genre> updateGenre(
            @PathVariable Long id,
            @RequestParam(value = "name") String name) {

        if (genreRepository.existsGenreByName(name)){
            throw new ResourceAlreadyExistsException("genre", name);
        }

        Genre genre = genreRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("genre",String.valueOf(id),"id"));

        genre.setName(name);

        return new SuccessResponse<>("genre updated", genreRepository.save(genre));
    }

    @DeleteMapping("/genre/{id}")
    SuccessResponse<Boolean> deleteGenre(@PathVariable Long id) {

        genreRepository.deleteById(id);
        return new SuccessResponse<>("genre deleted", true);
    }

}
