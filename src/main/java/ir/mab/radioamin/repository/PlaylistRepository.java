package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Playlist;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PlaylistRepository extends PagingAndSortingRepository<Playlist,Long> {

    List<Playlist> findTop20By();
}
