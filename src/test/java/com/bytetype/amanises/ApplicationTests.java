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

	private boolean roleExists(String roleName) {
		Integer count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM roles WHERE name = ?",
				Integer.class,
				roleName
		);
		return count != null && count > 0;
	}
}
