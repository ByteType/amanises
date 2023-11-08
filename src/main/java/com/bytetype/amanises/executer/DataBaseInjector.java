package com.bytetype.amanises.executer;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataBaseInjector {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseInjector.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @EventListener(ApplicationStartedEvent.class)
    public void init() throws RoleNotFoundException {
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

        logger.info("Roles has been injected to database.");
    }

    private void insertDriverIfNotExists() throws RoleNotFoundException {
        if (userRepository.findByRoleType(RoleType.ROLE_DRIVER).isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.ROLE_DRIVER).orElseThrow(RoleNotFoundException::new));
            User user = new User();
            user.setUsername("Driver");
            user.setPassword("Password");
            user.setRoles(roles);
            userRepository.saveAndFlush(user);

            logger.info("Default driver account has been injected to database.");
        }
    }
}
