package com.kml.capacity.service;

import com.kml.capacity.dto.UserActivityLogDto;
import java.util.List;

public interface UserActivityLogService {

  List<UserActivityLogDto> getAllUserActivityLogs();

  List<UserActivityLogDto> getActivityLogsByUser(Long userId);
}
