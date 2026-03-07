package com.kml.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.UserRequestDto;
import com.kml.capacity.dto.UserResponseDto;
import com.kml.capacity.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto requestDto) {

    UserResponseDto created =
        userService.createUser(
            requestDto.getName(),
            requestDto.getUsername(),
            requestDto.getPassword(),
            requestDto.getUserRole());

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}
