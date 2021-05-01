package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.MusicUrl;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MusicUrlRepository extends PagingAndSortingRepository<MusicUrl,Long> {
}
