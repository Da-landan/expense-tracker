package com.dalandan.expense_tracker.controller;

import com.dalandan.expense_tracker.model.User;
import com.dalandan.expense_tracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        User user = new User();
        user.setUsername("lance");
        user.setEmail("lance@example.com");
        user.setPassword(
                passwordEncoder.encode("correctPassword")
        );

        userRepository.save(user);
    }

    @Test
    void loginFailsWithWrongPassword() throws Exception {

        String body = """
                {
                    "username": "lance",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isUnauthorized());
    }
}