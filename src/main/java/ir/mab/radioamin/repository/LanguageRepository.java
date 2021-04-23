package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LanguageRepository extends PagingAndSortingRepository<Language,Long> {
    boolean existsLanguageByName(String name);
    Page<Language> findLanguagesByNameContaining(String name, Pageable pageable);
}
