package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Singer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SingerRepository extends PagingAndSortingRepository<Singer,Long> {

    boolean existsSingerByName(String name);
}
