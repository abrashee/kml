package com.kml.capacity.security;

import org.springframework.stereotype.Component;

import com.kml.api.error.OwnershipException;
import com.kml.domain.common.OwnedEntity;
import com.kml.domain.user.User;

@Component
public class OwnershipValidator {

  public void checkOwnership(User user, OwnedEntity entity) {
    if (!entity.getOwner().getId().equals(user.getId())) {
      throw new OwnershipException("User does not own this entity");
    }
  }
}
