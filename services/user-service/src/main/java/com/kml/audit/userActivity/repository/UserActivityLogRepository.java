package com.kml.audit.userActivity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.audit.userActivity.entity.UserActivityLog;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
  List<UserActivityLog> findByUserId(Long userId);
}
