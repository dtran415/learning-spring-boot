package com.mshift.acf.user_services.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends MongoRepository<User, Long> {

    Optional<User> findUserByUsername(String username);
    void deleteByUsername(String username);
}
