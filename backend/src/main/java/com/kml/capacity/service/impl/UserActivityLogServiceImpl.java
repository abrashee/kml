package com.kml.capacity.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.dto.UserActivityLogDto;
import com.kml.capacity.service.UserActivityLogService;
import com.kml.infra.UserActivityLogRepository;

@Service
public class UserActivityLogServiceImpl implements UserActivityLogService {

  private final UserActivityLogRepository userActivityLogRepository;

  public UserActivityLogServiceImpl(UserActivityLogRepository userActivityLogRepository) {
    this.userActivityLogRepository = userActivityLogRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserActivityLogDto> getAllUserActivityLogs() {
    return userActivityLogRepository.findAll().stream()
        .map(
            log ->
                new UserActivityLogDto(
                    log.getId(),
                    log.getUser().getId(),
                    log.getAction(),
                    log.getCreatedAt(), // pass createdAt in constructor
                    log.getUpdatedAt() // pass updatedAt in constructor
                    ))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserActivityLogDto> getActivityLogsByUser(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID must not be null");
    }

    return userActivityLogRepository.findByUserId(userId).stream()
        .map(
            log ->
                new UserActivityLogDto(
                    log.getId(),
                    log.getUser().getId(),
                    log.getAction(),
                    log.getCreatedAt(),
                    log.getUpdatedAt()))
        .collect(Collectors.toList());
  }
}
