package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Playlist;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlaylistRepository extends PagingAndSortingRepository<Playlist,Long> {

}
