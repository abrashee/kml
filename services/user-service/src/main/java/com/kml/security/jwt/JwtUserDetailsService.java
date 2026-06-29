package com.kml.security.jwt;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kml.user.entity.User;
import com.kml.user.entity.Manager;
import com.kml.user.entity.Worker;
import com.kml.user.entity.UserAccountStatus;
import com.kml.user.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public JwtUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found: " + username));

    if (user.getAccountStatus() == UserAccountStatus.SUSPENDED_POTENTIAL_BOT) {
      throw new UsernameNotFoundException("User account suspended: " + username);
    }

    List<SimpleGrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));

    Long warehouseId = null;
    Long managerId = null;

    if (user instanceof Manager manager) {
      warehouseId = manager.getWarehouseId();
    } else if (user instanceof Worker worker) {
      warehouseId = worker.getWarehouseId();
      managerId = worker.getManagerId();
    }

    return new KmlUserDetails(
        user.getUsername(),
        user.getPassword(),
        authorities,
        user.getId(),
        warehouseId,
        managerId);
  }
}
