package com.kml.security;

import org.springframework.stereotype.Component;

import com.kml.common.exception.OwnershipException;
import com.kml.common.security.HasOwner;
import com.kml.user.entity.User;

@Component
public class OwnershipValidator {

  public void checkOwnership(User user, HasOwner entity) {
    if (!entity.getOwner().getId().equals(user.getId())) {
      throw new OwnershipException("User does not own this entity");
    }
  }
}
