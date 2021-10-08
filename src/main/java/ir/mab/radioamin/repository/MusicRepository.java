package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Music;
import ir.mab.radioamin.model.enums.MusicType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MusicRepository extends PagingAndSortingRepository<Music,Long> {

    Page<Music> findMusicByNameContaining(String name, Pageable pageable);

    List<Music> findTop20By();

    List<Music> findTop20ByMusicTypeIs(MusicType musicType);

    List<Music> findTop20ByMusicTypeIsOrderByIdDesc(MusicType musicType);
}
