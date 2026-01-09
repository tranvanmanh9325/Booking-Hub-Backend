package com.example.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testLoginFailure() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "wrong@email.com");
        loginRequest.put("password", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(java.util.Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(java.util.Objects.requireNonNull(objectMapper.writeValueAsString(loginRequest))))
                .andExpect(status().isUnauthorized());
        // Note: Actual status depends on implementation, might be 400 or 401
        // Adjusting expectation to what is likely implemented or 4xx
    }
}
