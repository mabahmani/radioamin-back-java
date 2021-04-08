package ir.mab.radioamin.repository;

import ir.mab.radioamin.entity.BlackRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface BlackRefreshTokenRepository extends CrudRepository<BlackRefreshToken,Long> {
    boolean existsBlackRefreshTokenByRefreshToken(String refreshToken);
}
