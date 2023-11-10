package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.User;

public class UserPayload {

    private Long id;

    private String username;

    private String email;

    private String phone;

    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static UserPayload createFrom(User user) {
        UserPayload payload = new UserPayload();
        payload.setId(user.getId());
        payload.setUsername(user.getUsername());
        payload.setEmail(user.getEmail());
        payload.setPhone(user.getPhone());
        payload.setAddress(user.getAddress());

        return payload;
    }
}
