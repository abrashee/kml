package com.kml.common.entity;

import com.kml.user.entity.User;

public interface OwnedEntity {
  User getOwner();
}
