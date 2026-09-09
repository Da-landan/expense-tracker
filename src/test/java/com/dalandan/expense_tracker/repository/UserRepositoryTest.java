package com.dalandan.expense_tracker.repository;

import com.dalandan.expense_tracker.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void savesUser() {

        User user = new User();
        user.setUsername("lance");
        user.setEmail("lance@example.com");
        user.setPassword("hashed-password");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("lance", savedUser.getUsername());
    }
}