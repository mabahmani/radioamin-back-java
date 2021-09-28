package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Mood;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MoodRepository extends PagingAndSortingRepository<Mood,Long> {
    boolean existsByName(String name);

    List<Mood> findTop20By();
}
