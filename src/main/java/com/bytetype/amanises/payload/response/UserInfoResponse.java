package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.payload.common.UserPayload;

import java.util.List;

public class UserInfoResponse extends UserPayload {

    private List<String> roles;

    private String token;

    public UserInfoResponse(
            Long id,
            String username,
            String email,
            String phone,
            String address,
            List<String> roles
    ) {
        this.roles = roles;
        this.setId(id);
        this.setUsername(username);
        this.setEmail(email);
        this.setPhone(phone);
        this.setAddress(address);
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}