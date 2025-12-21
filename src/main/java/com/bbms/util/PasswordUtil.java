package com.bbms.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing utility using BCrypt.
 */
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Hash a plain text password.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify a plain text password against a hashed password.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * Check if a password meets minimum requirements.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        // Minimum 6 characters
        return password.length() >= 6;
    }

    /**
     * Generate a temporary password.
     */
    public static String generateTemporaryPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.security.SecureRandom();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
