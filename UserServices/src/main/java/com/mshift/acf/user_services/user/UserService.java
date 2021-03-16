package com.mshift.acf.user_services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService
{
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(User user) {

        // check to see if user already exist
        Optional<User> userByUsername = userDao.findUserByUsername(user.getUsername());
        if (userByUsername.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exist.");
        }

        // save user to database
        // create a new user and only save the necessary parameters
        User userToSave = new User(user.getUsername(), user.getFirstName(), user.getLastName());

        return userDao.save(userToSave);
    }

    public void delete(String username) {

        Optional<User> userByUsername = userDao.findUserByUsername(username);
        if (!userByUsername.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }

        userDao.deleteByUsername(username);
    }
}
