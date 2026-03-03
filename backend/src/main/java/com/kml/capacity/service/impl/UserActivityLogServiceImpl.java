package com.kml.capacity.service.impl;

import com.kml.capacity.service.UserActivityLogService;
import com.kml.domain.audit.UserActivityLog;
import com.kml.infra.UserActivityLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserActivityLogServiceImpl implements UserActivityLogService {
  private final UserActivityLogRepository userActivityLogRepository;

  public UserActivityLogServiceImpl(UserActivityLogRepository userActivityLogRepository) {
    this.userActivityLogRepository = userActivityLogRepository;
  }

  @Override
  public List<UserActivityLog> getAllUserActivityLogs() {
    List<UserActivityLog> userActivityLogs = this.userActivityLogRepository.findAll();
    return userActivityLogs;
  }

  @Override
  public List<UserActivityLog> getActivityLogsByUser(Long userId) {
    List<UserActivityLog> userActivityLogs = this.userActivityLogRepository.findByUserId(userId);
    return userActivityLogs;
  }
}
