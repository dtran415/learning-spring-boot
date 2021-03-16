package com.mshift.acf.auth.application_user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationUserDao extends MongoRepository<ApplicationUser, String> {

    Optional<ApplicationUser> findApplicationUserByUsername(String username);
}
