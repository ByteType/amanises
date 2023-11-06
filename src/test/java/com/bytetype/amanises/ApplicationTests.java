package com.bytetype.amanises;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles
class ApplicationTests {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void rolesShouldBePopulatedAtStartup() {
		assertThat(roleExists("GUEST")).isTrue();
		assertThat(roleExists("USER")).isTrue();
		assertThat(roleExists("DRIVER")).isTrue();
	}

	@Test
	public void driverShouldBeCreateAtStartup() {
		assertThat(driverExists("Driver")).isTrue();
	}

	private boolean roleExists(String roleName) {
		Integer count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM roles WHERE name = ?",
				Integer.class,
				roleName
		);
		return count != null && count > 0;
	}

	private boolean driverExists(String userName) {
		String verifyQuery = "SELECT COUNT(u.id) FROM users u " +
				"JOIN user_roles ur ON u.id = ur.user_id " +
				"JOIN roles r ON ur.role_id = r.id " +
				"WHERE u.username = ?";

		Integer count = jdbcTemplate.queryForObject(verifyQuery, Integer.class, userName);
		return count != null && count > 0;
	}
}
