package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User,Long> {

    Optional<User> findUserByEmail(String email);

    Page<User> findAllByEmailContaining(String email, Pageable pageable);

    Page<User> findAllByUserRolesIs(Role role, Pageable pageable);

    Page<User> findAllByEmailContainingAndUserRolesIs(String email, Role role, Pageable pageable);
}
