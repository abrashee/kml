package com.kml.domain.common;

import com.kml.domain.user.User;

public interface OwnedEntity {
  User getOwner();
}
