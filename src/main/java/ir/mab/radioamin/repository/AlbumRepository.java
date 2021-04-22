package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AlbumRepository extends PagingAndSortingRepository<Album,Long> {

    Page<Album> findAlbumsBySinger_Name(String singerName, Pageable pageable);
    Page<Album> findAlbumsByNameContaining(String albumName, Pageable pageable);
    Page<Album> findAlbumsByNameContainingAndSinger_Name(String albumName, String singerName, Pageable pageable);
}
