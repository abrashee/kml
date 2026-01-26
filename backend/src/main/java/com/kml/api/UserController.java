package com.kml.api;

import com.kml.capacity.dto.UserRequestDto;
import com.kml.capacity.dto.UserResponseDto;
import com.kml.capacity.service.UserService;
import com.kml.domain.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto requestDto) {
    User user =
        this.userService.createUser(
            requestDto.getName(),
            requestDto.getUsername(),
            requestDto.getPassword(),
            requestDto.getUserRole());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(user));
  }

  private UserResponseDto mapToResponseDto(User user) {
    UserResponseDto responseDto = new UserResponseDto();

    responseDto.setId(user.getId());
    responseDto.setName(user.getName());
    responseDto.setUsername(user.getUsername());
    responseDto.setUserRole(user.getUserRole().name());
    responseDto.setCreatedAt(user.getCreatedAt());
    responseDto.setUpdatedAt(user.getUpdatedAt());
    return responseDto;
  }
}
