package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {

    Optional<User> findUserByEmail(String email);
}
