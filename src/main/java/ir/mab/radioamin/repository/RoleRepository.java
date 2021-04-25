package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.model.enums.RoleEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role,Long> {
    Optional<Role> findRoleByRole(RoleEnum roleEnum);
}
