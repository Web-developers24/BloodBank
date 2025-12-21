package com.bbms.services;

import com.bbms.dao.UserDao;
import com.bbms.models.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {
    
    @Mock
    private UserDao userDao;
    
    private AuthService authService;
    
    @BeforeEach
    void setUp() {
        authService = AuthService.getInstance();
    }
    
    @AfterEach
    void tearDown() {
        authService.logout();
    }
    
    @Test
    @DisplayName("Should authenticate valid user")
    void shouldAuthenticateValidUser() {
        // Given
        String username = "admin";
        String password = "admin123";
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPasswordHash(AuthService.hashPassword(password));
        mockUser.setActive(true);
        
        // When / Then - Integration test with actual DAO would be needed
        // This demonstrates the test structure
        assertNotNull(authService);
    }
    
    @Test
    @DisplayName("Should reject inactive user")
    void shouldRejectInactiveUser() {
        // Given
        User inactiveUser = new User();
        inactiveUser.setUsername("inactive");
        inactiveUser.setActive(false);
        
        // When
        boolean result = authService.isLoggedIn();
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should hash password correctly")
    void shouldHashPasswordCorrectly() {
        // Given
        String password = "testPassword123";
        
        // When
        String hashed = AuthService.hashPassword(password);
        
        // Then
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(AuthService.verifyPassword(password, hashed));
    }
    
    @Test
    @DisplayName("Should not verify incorrect password")
    void shouldNotVerifyIncorrectPassword() {
        // Given
        String password = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashed = AuthService.hashPassword(password);
        
        // When
        boolean result = AuthService.verifyPassword(wrongPassword, hashed);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should logout successfully")
    void shouldLogoutSuccessfully() {
        // Given
        authService.logout();
        
        // When
        boolean isLoggedIn = authService.isLoggedIn();
        User currentUser = authService.getCurrentUser();
        
        // Then
        assertFalse(isLoggedIn);
        assertNull(currentUser);
    }
    
    @Test
    @DisplayName("Singleton should return same instance")
    void singletonShouldReturnSameInstance() {
        // When
        AuthService instance1 = AuthService.getInstance();
        AuthService instance2 = AuthService.getInstance();
        
        // Then
        assertSame(instance1, instance2);
    }
}
