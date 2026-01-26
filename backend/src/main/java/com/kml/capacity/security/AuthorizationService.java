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

  // Ownership rules

  public static boolean canAccessWarehouse(User user, Long warehouseId) {
    return user.getUserRole() == UserRole.ADMIN || user.getUserRole() == UserRole.MANAGER;
  }

  public static boolean canAccessOrder(User user, Long orderId) {
    return user.getUserRole() == UserRole.MANAGER
        || user.getUserRole() == UserRole.USER
        || user.getUserRole() == UserRole.CUSTOMER;
  }

  public static boolean canAccessShipment(User user, Long shipmentId) {
    return user.getUserRole() == UserRole.MANAGER
        || user.getUserRole() == UserRole.USER
        || user.getUserRole() == UserRole.CUSTOMER;
  }
}
