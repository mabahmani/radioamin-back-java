package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GenreRepository extends PagingAndSortingRepository<Genre,Long> {
    boolean existsGenreByName(String name);
    Page<Genre> findGenresByNameContaining(String name, Pageable pageable);

    List<Genre> findTop20By();
}
