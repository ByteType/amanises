package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.User;

public class ParcelUserResponse {
    private Long id;

    private String username;

    private String email;

    private String phone;

    private String address;

    public ParcelUserResponse(Long id, String username, String email, String phone, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

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

    public static ParcelUserResponse createFrom(User user) {
        return new ParcelUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );
    }
}