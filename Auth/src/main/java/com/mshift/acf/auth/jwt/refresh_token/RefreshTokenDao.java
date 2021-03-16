package com.mshift.acf.auth.jwt.refresh_token;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenDao extends MongoRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByJti(String jti);
    void deleteByJti(String jti);
}
