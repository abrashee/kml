package com.kml.capacity.security;

import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;

public class AuthorizationService {

  public static boolean canCreateWarehouse(User user) {
    return user.getUserRole() == UserRole.ADMIN || user.getUserRole() == UserRole.MANAGER;
  }

  public static boolean canCreateOrder(User user) {
    return user.getUserRole() == UserRole.CUSTOMER || user.getUserRole() == UserRole.USER;
  }

  public static boolean canCreateShipment(User user) {
    return user.getUserRole() == UserRole.ADMIN || user.getUserRole() == UserRole.MANAGER;
  }
}
