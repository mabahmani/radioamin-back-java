package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Music;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MusicRepository extends PagingAndSortingRepository<Music,Long> {
}
