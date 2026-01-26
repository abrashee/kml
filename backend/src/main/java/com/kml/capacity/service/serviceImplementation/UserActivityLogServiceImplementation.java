package com.kml.capacity.service.serviceImplementation;

import com.kml.capacity.service.UserActivityLogService;
import com.kml.domain.audit.UserActivityLog;
import com.kml.infra.UserActivityLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserActivityLogServiceImplementation implements UserActivityLogService {
  private final UserActivityLogRepository userActivityLogRepository;

  public UserActivityLogServiceImplementation(UserActivityLogRepository userActivityLogRepository) {
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
