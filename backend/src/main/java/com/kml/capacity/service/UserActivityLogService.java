package com.kml.capacity.service;

import java.util.List;

import com.kml.domain.audit.UserActivityLog;

public interface UserActivityLogService {

  List<UserActivityLog> getAllUserActivityLogs();

  List<UserActivityLog> getActivityLogsByUser(Long userId);
}
