package com.bytetype.amanises.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RoleInject implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public RoleInject(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        insertRoleIfNotExists("GUEST");
        insertRoleIfNotExists("USER");
        insertRoleIfNotExists("DRIVER");
    }

    private void insertRoleIfNotExists(String roleName) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles WHERE name = ?", Integer.class, roleName);
        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO roles(name) VALUES(?)", roleName);
        }
    }
}
