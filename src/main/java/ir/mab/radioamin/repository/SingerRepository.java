package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Singer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SingerRepository extends PagingAndSortingRepository<Singer,Long> {

    boolean existsSingerByName(String name);

    Page<Singer> findSingersByNameContaining(String name, Pageable pageable);

    List<Singer> findTop20ByOrderByFollowCountsDesc();
}
