package com.prept.tracker.controller;

import com.prept.tracker.domain.entity.User;
import com.prept.tracker.dto.request.ChangePasswordRequest;
import com.prept.tracker.dto.request.LoginRequest;
import com.prept.tracker.dto.request.RegisterRequest;
import com.prept.tracker.dto.response.AuthResponse;
import com.prept.tracker.dto.response.UserResponse;
import com.prept.tracker.repository.UserRepository;
import com.prept.tracker.security.service.AuthenticationService;
import com.prept.tracker.security.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication", description = "Auth endpoints")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT tokens")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generates new access and refresh tokens from a valid refresh token")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns details of the currently authenticated user")
    public ResponseEntity<UserResponse> me() {
        Long userId = authorizationService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.getEnabled())
                .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes the password for the currently authenticated user")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = authorizationService.getCurrentUserId();
        authenticationService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }
}
