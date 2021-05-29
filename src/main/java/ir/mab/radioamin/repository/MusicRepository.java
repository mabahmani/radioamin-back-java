package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MusicRepository extends PagingAndSortingRepository<Music,Long> {

    Page<Music> findMusicByNameContaining(String name, Pageable pageable);
}
