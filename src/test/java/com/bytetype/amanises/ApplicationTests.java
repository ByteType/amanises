package com.bytetype.amanises;

import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	public void rolesShouldBePopulatedAtStartup() {
		for (RoleType roleType : RoleType.values()) {
			assertThat(roleRepository.findByName(roleType).isPresent()).isTrue();
		}
	}

	@Test
	public void driverShouldBeCreateAtStartup() {
		assertThat(userRepository.findByUsername("Driver").isPresent()).isTrue();
	}
}
