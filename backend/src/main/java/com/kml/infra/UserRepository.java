package com.kml.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String username);

  List<User> findByRole(UserRole userRole);
}
