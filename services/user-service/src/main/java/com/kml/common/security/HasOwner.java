package com.kml.common.security;

import com.kml.user.entity.User;

public interface HasOwner {
  User getOwner();
}
