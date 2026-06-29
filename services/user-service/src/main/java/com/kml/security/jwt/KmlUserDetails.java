package com.kml.security.jwt;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.kml.services.common.security.jwt.JwtScopedUserDetails;

public class KmlUserDetails extends User implements JwtScopedUserDetails {

  private final Long userId;
  private final Long warehouseId;
  private final Long managerId;

  public KmlUserDetails(
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      Long userId,
      Long warehouseId,
      Long managerId) {
    super(username, password, authorities);
    this.userId = userId;
    this.warehouseId = warehouseId;
    this.managerId = managerId;
  }

  @Override
  public Long userId() {
    return userId;
  }

  @Override
  public Long warehouseId() {
    return warehouseId;
  }

  @Override
  public Long managerId() {
    return managerId;
  }
}
