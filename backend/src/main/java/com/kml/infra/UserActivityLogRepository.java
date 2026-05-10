package com.kml.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.audit.UserActivityLog;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
  List<UserActivityLog> findByUserId(Long userId);
}
