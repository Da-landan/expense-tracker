package com.dalandan.expense_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void expensesRejectsRequestWithoutJwt() throws Exception {

        mockMvc.perform(
                        get("/expenses")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expensesRejectsInvalidJwt() throws Exception {

        mockMvc.perform(
                        get("/expenses")
                                .header(
                                        "Authorization",
                                        "Bearer definitely-not-a-valid-jwt"
                                )
                )
                .andExpect(status().isUnauthorized());
    }
}