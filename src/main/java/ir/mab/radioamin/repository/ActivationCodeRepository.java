package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.ActivationCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ActivationCodeRepository extends CrudRepository<ActivationCode, Long> {

    Optional<ActivationCode> findActivationCodeByCode(String code);
}
