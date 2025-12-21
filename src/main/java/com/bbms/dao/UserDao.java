package com.bbms.dao;

import com.bbms.model.User;
import java.util.Optional;

public class UserDao extends AbstractDao<User, Long> {

    public Optional<User> findByUsername(String username) {
        return executeSingleQuery(
                "FROM User WHERE username = :username AND isActive = true",
                "username", username
        );
    }

    public Optional<User> findByEmail(String email) {
        return executeSingleQuery(
                "FROM User WHERE email = :email",
                "email", email
        );
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
}
