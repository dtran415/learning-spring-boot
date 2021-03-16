package com.mshift.acf.auth.application_user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ApplicationUserService implements UserDetailsService {

    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public ApplicationUserService(ApplicationUserDao applicationUserDao) {
        this.applicationUserDao = applicationUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserDao.findApplicationUserByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", username)));
    }

    public ApplicationUser createUser(ApplicationUser user) {

        if (applicationUserDao.findApplicationUserByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used.");
        }

        return applicationUserDao.save(user);
    }

    public void deleteUser(String username) {

        Optional<ApplicationUser> applicationUser = applicationUserDao.findApplicationUserByUsername(username);
        if (!applicationUser.isPresent()) {
            return;
        }

        applicationUserDao.delete(applicationUser.get());
    }

    public void deleteUserById(String id) {
        applicationUserDao.deleteById(id);
    }
}
