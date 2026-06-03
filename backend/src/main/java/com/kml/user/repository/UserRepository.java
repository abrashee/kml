package com.kml.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.user.entity.User;
import com.kml.user.entity.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String username);

  List<User> findByUserRole(UserRole userRole);

  Optional<User> findByUsername(String username);

  Page<User> findAll(Pageable pageable);

  Page<User> findByUserRole(UserRole role, Pageable pageable);
}
