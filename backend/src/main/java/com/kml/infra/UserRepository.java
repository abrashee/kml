package com.kml.infra;

import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String username);

  List<UserRole> findByUserRole(UserRole userRole);
}
