package com.dalandan.expense_tracker.service;

import com.dalandan.expense_tracker.dto.LoginUserRequest;
import com.dalandan.expense_tracker.exception.InvalidCredentialsException;
import com.dalandan.expense_tracker.model.User;
import com.dalandan.expense_tracker.repository.UserRepository;
import com.dalandan.expense_tracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));

        return userRepository.save(user);
    }

    public String userLogin(LoginUserRequest request){

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username or password"
                        )
                );

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return token;
    }
}