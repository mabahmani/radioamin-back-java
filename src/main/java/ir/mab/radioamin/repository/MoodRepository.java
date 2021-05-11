package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Mood;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MoodRepository extends PagingAndSortingRepository<Mood,Long> {

}
