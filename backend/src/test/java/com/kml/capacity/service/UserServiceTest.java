package com.kml.capacity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kml.capacity.dto.user.UserResponseDto;
import com.kml.capacity.service.impl.UserServiceImpl;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.UserRepository;

public class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private UserServiceImpl userService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateUserSuccessfully() {
    String hashedPassword = "$2a$10$abc123";
    when(passwordEncoder.encode("password123")).thenReturn(hashedPassword);
    when(userRepository.existsByUsername("john")).thenReturn(false);

    User user = new User("John Doe", "john", hashedPassword, UserRole.USER);
    when(userRepository.save(any(User.class))).thenReturn(user);

    UserResponseDto saved =
        userService.createUser("John Doe", "john", "password123", UserRole.USER);

    assertNotNull(saved);
    assertEquals("john", saved.username());
    assertEquals("USER", saved.userRole()); // DTO uses String
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testCreateUserFailsIfUsernameExists() {
    when(userRepository.existsByUsername("john")).thenReturn(true);

    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser("John Doe", "john", "password123", UserRole.USER));

    assertTrue(ex.getMessage().contains("exists"));
  }

  @Test
  void testExistsByUsername() {
    when(userRepository.existsByUsername("john")).thenReturn(true);
    assertTrue(userService.existsByUsername("john"));
  }
}
