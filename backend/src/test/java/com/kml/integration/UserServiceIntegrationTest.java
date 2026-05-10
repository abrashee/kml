package com.kml.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kml.capacity.dto.user.UserResponseDto;
import com.kml.capacity.service.UserService;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

  @Autowired private UserService userService;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    // Clear existing users before each test
    userRepository.deleteAll();
  }

  @Test
  void testCreateUserSuccessfully() {
    UserResponseDto created =
        userService.createUser("John Doe", "johndoe", "password123", UserRole.USER);

    assertNotNull(created);
    assertNotNull(created.id());
    assertEquals("John Doe", created.name());
    assertEquals("johndoe", created.username());
    assertEquals("USER", created.userRole());
    assertNotNull(created.createdAt());
    assertNotNull(created.updatedAt());
  }

  @Test
  void testCreateUserWithNullName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser(null, "johndoe", "password123", UserRole.USER));
  }

  @Test
  void testCreateUserWithBlankName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser("   ", "johndoe", "password123", UserRole.USER));
  }

  @Test
  void testCreateUserWithNullUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser("John Doe", null, "password123", UserRole.USER));
  }

  @Test
  void testCreateUserWithBlankUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser("John Doe", "   ", "password123", UserRole.USER));
  }

  @Test
  void testCreateUserWithNullPassword() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser("John Doe", "johndoe", null, UserRole.USER));
  }

  @Test
  void testCreateUserWithBlankPassword() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser("John Doe", "johndoe", "   ", UserRole.USER));
  }

  @Test
  void testCreateUserWithNullRole() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser("John Doe", "johndoe", "password123", null));
  }

  @Test
  void testCreateUserWithDuplicateUsername() {
    // Create first user
    userService.createUser("John Doe", "johndoe", "password123", UserRole.USER);

    // Try to create another user with the same username
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.createUser("Jane Doe", "johndoe", "password456", UserRole.ADMIN));
  }

  @Test
  void testPasswordIsEncoded() {
    String plainPassword = "mySecurePassword123!";
    UserResponseDto created =
        userService.createUser("Jane Doe", "janedoe", plainPassword, UserRole.USER);

    // Verify the user was saved with encoded password
    User savedUser =
        userRepository
            .findById(created.id())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Password should NOT be the plain text
    assertFalse(savedUser.getPassword().equals(plainPassword));

    // Password should match when encoded
    assertTrue(passwordEncoder.matches(plainPassword, savedUser.getPassword()));
  }

  @Test
  void testExistsByUsernameReturnsTrue() {
    userService.createUser("John Doe", "johndoe", "password123", UserRole.USER);

    assertTrue(userService.existsByUsername("johndoe"));
  }

  @Test
  void testExistsByUsernameReturnsFalse() {
    assertFalse(userService.existsByUsername("nonexistent"));
  }

  @Test
  void testExistsByUsernameReturnsFalseAfterCreate() {
    assertFalse(userService.existsByUsername("newuser"));

    userService.createUser("New User", "newuser", "password123", UserRole.USER);

    assertTrue(userService.existsByUsername("newuser"));
  }

  @Test
  void testCreateMultipleUsersWithDifferentRoles() {
    UserResponseDto admin =
        userService.createUser("Admin User", "admin", "password123", UserRole.ADMIN);
    UserResponseDto user =
        userService.createUser("Regular User", "regularuser", "password456", UserRole.USER);

    assertNotNull(admin);
    assertNotNull(user);
    assertEquals("ADMIN", admin.userRole());
    assertEquals("USER", user.userRole());
    assertEquals("admin", admin.username());
    assertEquals("regularuser", user.username());
  }

  @Test
  void testUserIsPersistetToDatabase() {
    UserResponseDto created =
        userService.createUser("Persistent User", "persistentuser", "password123", UserRole.USER);

    // Verify it exists in the repository
    User found =
        userRepository
            .findById(created.id())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    assertEquals("Persistent User", found.getName());
    assertEquals("persistentuser", found.getUsername());
    assertEquals(UserRole.USER, found.getUserRole());
  }

  @Test
  void testCreateUserWithDifferentPasswordLengths() {
    // Short password
    UserResponseDto shortPass =
        userService.createUser("User One", "userone", "pass", UserRole.USER);
    assertNotNull(shortPass);

    // Long password
    UserResponseDto longPass =
        userService.createUser(
            "User Two",
            "usertwo",
            "thisIsAVeryLongPasswordWithManyCharactersThatShouldStillWork123!@#",
            UserRole.USER);
    assertNotNull(longPass);

    // Verify both are saved
    assertTrue(userService.existsByUsername("userone"));
    assertTrue(userService.existsByUsername("usertwo"));
  }

  @Test
  void testCreateUserWithSpecialCharactersInPassword() {
    String specialPassword = "P@ssw0rd!#$%^&*()";
    UserResponseDto created =
        userService.createUser("Special User", "specialuser", specialPassword, UserRole.USER);

    assertNotNull(created);
    User savedUser =
        userRepository
            .findById(created.id())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Verify special characters password is properly encoded and matches
    assertTrue(passwordEncoder.matches(specialPassword, savedUser.getPassword()));
  }

  @Test
  void testCreateUserAuditFields() {
    UserResponseDto created =
        userService.createUser("Audit User", "audituser", "password123", UserRole.USER);

    assertNotNull(created.createdAt());
    assertNotNull(created.updatedAt());
    assertEquals(created.createdAt(), created.updatedAt());
  }

  @Test
  void testMultipleUsersCannotShareUsername() {
    userService.createUser("First User", "shared", "password1", UserRole.USER);

    // Try to create another user with same username
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser("Second User", "shared", "password2", UserRole.ADMIN));

    assertTrue(exception.getMessage().contains("exists"));
  }

  @Test
  void testCreateUserWithMinimalValidData() {
    UserResponseDto created = userService.createUser("A", "abc", "123", UserRole.USER);

    assertNotNull(created);
    assertEquals("A", created.name());
    assertEquals("abc", created.username());
    assertEquals("USER", created.userRole());
  }
}
