package com.bbms.service;

import com.bbms.dao.UserDao;
import com.bbms.model.User;
import com.bbms.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);
    private static AuthService instance;
    
    private final UserDao userDao;
    private User currentUser;

    private AuthService() {
        this.userDao = new UserDao();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        Optional<User> userOpt = userDao.findByUsername(username.trim());
        
        if (userOpt.isEmpty()) {
            logger.warn("Login failed: User not found - {}", username);
            return false;
        }

        User user = userOpt.get();
        
        // For demo purposes, allow plain text comparison if BCrypt fails
        boolean passwordMatch = PasswordUtil.verifyPassword(password, user.getPasswordHash());
        if (!passwordMatch && password.equals(user.getPasswordHash())) {
            passwordMatch = true; // Allow plain text for initial setup
        }
        
        // Special case for initial admin setup
        if (!passwordMatch && username.equals("admin") && password.equals("admin123")) {
            passwordMatch = true;
        }

        if (!passwordMatch) {
            logger.warn("Login failed: Invalid password for user - {}", username);
            return false;
        }

        if (!user.getIsActive()) {
            logger.warn("Login failed: User account is disabled - {}", username);
            return false;
        }

        currentUser = user;
        user.setLastLogin(LocalDateTime.now());
        userDao.update(user);
        
        logger.info("User logged in successfully: {}", username);
        return true;
    }

    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.UserRole.ADMIN;
    }

    public boolean hasRole(User.UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }

        if (!PasswordUtil.verifyPassword(oldPassword, currentUser.getPasswordHash())) {
            return false;
        }

        if (!PasswordUtil.isValidPassword(newPassword)) {
            return false;
        }

        currentUser.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        userDao.update(currentUser);
        logger.info("Password changed for user: {}", currentUser.getUsername());
        return true;
    }

    public User createUser(String username, String password, String fullName, User.UserRole role) {
        if (userDao.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setFullName(fullName);
        user.setRole(role);
        user.setIsActive(true);

        return userDao.save(user);
    }
}
