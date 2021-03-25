package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsUserByEmail(String email);

}
