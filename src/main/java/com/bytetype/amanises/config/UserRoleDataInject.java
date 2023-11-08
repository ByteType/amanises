package com.bytetype.amanises.config;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserRoleDataInject implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws RoleNotFoundException {
        insertRolesIfNotExists();
        insertDriverIfNotExists();
    }

    private void insertRolesIfNotExists() {
        List<Role> roles = Arrays.stream(RoleType.values())
                .map(roleType -> {
                    Role role = new Role();
                    role.setName(roleType);
                    return roleRepository.findByName(roleType).orElse(role);
                })
                .toList();

        roleRepository.saveAllAndFlush(roles);
    }

    private void insertDriverIfNotExists() throws RoleNotFoundException {
        if (userRepository.findByRoleType(RoleType.DRIVER).isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.DRIVER).orElseThrow(RoleNotFoundException::new));
            User user = new User();
            user.setUsername("Driver");
            user.setPassword("Password");
            user.setRoles(roles);
            userRepository.saveAndFlush(user);
        }
    }
}
