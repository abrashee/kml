package com.kml.services.common.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtScopedUserDetails extends UserDetails {

  Long userId();

  Long warehouseId();

  Long managerId();
}
