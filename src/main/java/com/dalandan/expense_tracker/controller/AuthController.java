package com.dalandan.expense_tracker.controller;

import com.dalandan.expense_tracker.dto.LoginUserRequest;
import com.dalandan.expense_tracker.dto.RegisterUserRequest;
import com.dalandan.expense_tracker.model.User;
import com.dalandan.expense_tracker.repository.UserRepository;
import com.dalandan.expense_tracker.security.JwtUtil;
import com.dalandan.expense_tracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserRequest request) {

        String token = authService.userLogin(request);

        return ResponseEntity.ok(token);
    }
}