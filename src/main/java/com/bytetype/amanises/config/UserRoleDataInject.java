package com.bytetype.amanises.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserRoleDataInject implements CommandLineRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // Inject default roles
        insertRoleIfNotExists("GUEST");
        insertRoleIfNotExists("USER");
        insertRoleIfNotExists("DRIVER");
        // Inject default account
        insertDriverIfNotExists();
    }

    private void insertRoleIfNotExists(String roleName) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles WHERE name = ?", Integer.class, roleName);
        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO roles(name) VALUES(?)", roleName);
        }
    }

    private void insertDriverIfNotExists() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users u " +
                "JOIN user_roles ur ON u.id = ur.user_id " +
                "JOIN roles r ON ur.role_id = r.id " +
                "WHERE r.name = ?",
                Integer.class,
                "DRIVER"
        );
        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO users (username, password) VALUES (?, ?)", "Driver", "Password");
            Long newUserId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = ?", Long.class, "Driver");
            Long roleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = ?", Long.class, "DRIVER");
            jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", newUserId, roleId);
        }
    }
}
