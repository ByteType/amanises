package com.bytetype.amanises;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.payload.request.LoginRequest;
import com.bytetype.amanises.payload.request.SignupRequest;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final String username = "testUser";
    private final String password = "testPassword";

    @BeforeAll
    public void init() throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(RoleNotFoundException::new));
        User user = new User();
        user.setUsername(username);
        user.setEmail("testUser@testDomain.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setAddress("123 Main St, Apt 4");
        user.setRoles(roles);

        userRepository.saveAndFlush(user);
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String body = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUser() throws Exception {
        HashSet<String> roles = new HashSet<>();
        roles.add("user");
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setEmail("newUser@example.com");
        signUpRequest.setPassword("newPassword");
        signUpRequest.setAddress("987 Main St, Apt 6");
        signUpRequest.setRole(roles);

        String body = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("newUser@example.com"));

        // Verify the user is saved in the database
        User registeredUser = userRepository.findByUsername("newUser").orElse(null);
        assert registeredUser != null;
        assert passwordEncoder.matches("newPassword", registeredUser.getPassword());
    }
}