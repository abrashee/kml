package com.kml.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.audit.userActivity.entity.UserActivityLog;
import com.kml.audit.userActivity.repository.UserActivityLogRepository;
import com.kml.user.dto.UserResponseDto;
import com.kml.user.entity.Manager;
import com.kml.user.entity.User;
import com.kml.user.entity.UserRole;
import com.kml.user.entity.Worker;
import com.kml.user.dto.UpdateStaffAccessDto;
import com.kml.user.dto.UserRequestDto;
import com.kml.user.mapper.UserMapper;
import com.kml.user.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserActivityLogRepository userActivityLogRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserActivityLogRepository userActivityLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userActivityLogRepository = userActivityLogRepository;
    }

    @Override
    @Transactional
    public UserResponseDto createUserWithDetails(UserRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User userEntity;

        switch (dto.getUserRole()) {
            case MANAGER -> {
                if (dto.getWarehouseId() == null) {
                    throw new IllegalArgumentException("Warehouse scope assignment required for Managers");
                }
                userEntity = new Manager(dto.getName(), dto.getUsername(), encodedPassword, dto.getWarehouseId());
            }
            case WORKER -> {
                userEntity = new Worker(dto.getName(), dto.getUsername(), encodedPassword, dto.getWarehouseId(), dto.getManagerId());
            }
            default -> {
                userEntity = new User(dto.getName(), dto.getUsername(), encodedPassword, dto.getUserRole());
            }
        }

        userEntity.setAddress(dto.getAddress());
        userEntity.setAvatarUrl(dto.getAvatarUrl());

        User saved = userRepository.save(userEntity);
        audit(saved, "USER_CREATED", "User", saved.getId());
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto updateMeProfile(String username, UserRequestDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        user.setName(dto.getName());
        user.setAddress(dto.getAddress());
        user.setAvatarUrl(dto.getAvatarUrl());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User saved = userRepository.save(user);
        audit(saved, "USER_PROFILE_UPDATED", "User", saved.getId());
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsersPage(String role, Pageable pageable) {
        if (role != null && !role.isBlank()) {
            return userRepository.findByUserRole(UserRole.valueOf(role.toUpperCase()), pageable).map(UserMapper::toDto);
        }
        return userRepository.findAll(pageable).map(UserMapper::toDto);
    }

    @Override
    @Transactional
    public void assignRole(Long id, UserRole newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User entity not found"));
        user.setUserRole(newRole);
        User saved = userRepository.save(user);
        audit(saved, "USER_ROLE_ASSIGNED", "User", saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User tracking ID absent"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Username target identifier absent"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public UserResponseDto updateOperationalAccess(Long id, UpdateStaffAccessDto dto) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Operational target staff node missing"));

        // Instead of destructive drop-and-recreate which invalidates entity states across cluster network,
        // we isolate properties or alter values dynamically.
        if (oldUser.getUserRole() == dto.getRole()) {
            if (oldUser instanceof Manager manager && dto.getWarehouseId() != null) {
                manager.setWarehouseId(dto.getWarehouseId());
            } else if (oldUser instanceof Worker worker) {
                if (dto.getWarehouseId() != null) worker.setWarehouseId(dto.getWarehouseId());
            }
            User saved = userRepository.save(oldUser);
            audit(saved, "USER_OPERATIONAL_ACCESS_UPDATED", "User", saved.getId());
            return UserMapper.toDto(saved);
        }

        // Safe Polymorphic reassignment path via database row update sequence
        userRepository.delete(oldUser);
        userRepository.flush();

        User newUser;
        switch (dto.getRole()) {
            case MANAGER -> {
                if (dto.getWarehouseId() == null) {
                    throw new IllegalArgumentException("Warehouse scope assignment required for Managers");
                }
                newUser = new Manager(oldUser.getName(), oldUser.getUsername(), oldUser.getPassword(), dto.getWarehouseId());
            }
            case WORKER -> {
                newUser = new Worker(oldUser.getName(), oldUser.getUsername(), oldUser.getPassword(), dto.getWarehouseId(), null);
            }
            default -> {
                newUser = new User(oldUser.getName(), oldUser.getUsername(), oldUser.getPassword(), dto.getRole());
            }
        }

        newUser.setAddress(oldUser.getAddress());
        newUser.setAvatarUrl(oldUser.getAvatarUrl());

        User saved = userRepository.save(newUser);
        audit(saved, "USER_OPERATIONAL_ACCESS_UPDATED", "User", saved.getId());
        return UserMapper.toDto(saved);
    }

    private void audit(User user, String action, String entity, Long entityId) {
        userActivityLogRepository.save(new UserActivityLog(user, user, action, entity, entityId));
    }
}
