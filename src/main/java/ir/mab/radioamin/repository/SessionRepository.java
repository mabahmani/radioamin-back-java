package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session,Long> {

    Optional<Session> findSessionByRefreshToken(String refreshToken);
}
