package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.user.UserActivityLogDto;

public interface UserActivityLogService {

  List<UserActivityLogDto> getAllUserActivityLogs();

  List<UserActivityLogDto> getActivityLogsByUser(Long userId);
}
