package com.bytetype.amanises.utility;

import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.User;

import java.util.HashSet;
import java.util.Set;

public class UserUtilities {

    public static User createUser(String name, String email, String password, String address, Role role) {
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAddress(address);
        user.setRoles(roles);
        return user;
    }
}
