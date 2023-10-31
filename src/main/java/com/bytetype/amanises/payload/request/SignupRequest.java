package com.bytetype.amanises.payload.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class SignupRequest {
    @NotBlank
    private String username;

    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String address;

    private Set<String> role;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public Set<String> getRole() {
        return this.role;
    }
}