package com.dalandan.expense_tracker.dto;

public class AuthResponse {

    private String token;
    private String username;

    public AuthResponse(String username, String token) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}