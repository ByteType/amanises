package com.bytetype.amanises;

import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import com.bytetype.amanises.service.EmailService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private EmailService emailService;

	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
			.withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "springboot"))
			.withPerMethodLifecycle(false);

	@Test
	public void rolesShouldBePopulatedAtStartup() {
		for (RoleType roleType : RoleType.values()) {
			assert roleRepository.findByName(roleType).isPresent();
		}
	}

	@Test
	public void driverShouldBeCreateAtStartup() {
		assert userRepository.findByUsername("Driver").isPresent();
	}

	@Test
	void testSendSimpleMail() {
		emailService.sendEmail("to@localhost", "Test", "Test text");
		final MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assert receivedMessages.length == 1;
	}
}
