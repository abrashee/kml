package com.kml.audit.userActivity.service;

import java.util.List;

import com.kml.audit.userActivity.dto.UserActivityLogDto;

public interface UserActivityLogService {

  List<UserActivityLogDto> getAllUserActivityLogs();

  List<UserActivityLogDto> getActivityLogsByUser(Long userId);
}
