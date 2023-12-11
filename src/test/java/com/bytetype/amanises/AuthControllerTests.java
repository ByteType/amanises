package com.bytetype.amanises;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.payload.request.LoginRequest;
import com.bytetype.amanises.payload.request.SignupRequest;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import com.bytetype.amanises.utility.UserUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTests {

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

    private static final String className = AuthControllerTests.class.getSimpleName();

    private static class DataSet {
        private static final String[] username = { className + "User1", className + "User2", className + "User3" };

        private static final String[] email = { className + "User1@testDomain.com", className + "User2@testDomain.com", className + "User3@testDomain.com" };

        private static final String[] password = { "testPassword1", "testPassword2", "testPassword3" };

        private static final String[] address = { "123 Main St, " + className + "Town, NA", "456 Main St, " + className + "Town, NA", "789 Main St, " + className + "Town, NA" };

        private static final String[] role = { "user", "user", "user" };
    }

    @BeforeAll
    public void init() throws RoleNotFoundException {
        Role guest = roleRepository.findByName(RoleType.ROLE_GUEST).orElseThrow(RoleNotFoundException::new);
        Role user = roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(RoleNotFoundException::new);

        userRepository.saveAndFlush(UserUtilities.createUser(
                DataSet.username[0],
                DataSet.email[0],
                passwordEncoder.encode(DataSet.password[0]),
                DataSet.address[0],
                user
        ));
        userRepository.saveAndFlush(UserUtilities.createUser(
                DataSet.username[1],
                DataSet.address[1],
                guest
        ));
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(DataSet.username[0]);
        loginRequest.setPassword(DataSet.password[0]);

        String body = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterGuestUser() throws Exception {
        HashSet<String> roles = new HashSet<>();
        roles.add(DataSet.role[1]);
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername(DataSet.username[1]);
        signUpRequest.setEmail(DataSet.email[1]);
        signUpRequest.setPassword(DataSet.password[1]);
        signUpRequest.setAddress(DataSet.address[1]);
        signUpRequest.setRole(roles);

        String body = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.username").value(DataSet.username[1]))
                .andExpect(jsonPath("$.email").value(DataSet.email[1]));

        // Verify the user is saved in the database
        User registeredUser = userRepository.findByUsername(DataSet.username[1]).orElse(null);
        assert registeredUser != null;
        assert passwordEncoder.matches(DataSet.password[1], registeredUser.getPassword());
    }

    @Test
    public void testRegisterNewUser() throws Exception {
        HashSet<String> roles = new HashSet<>();
        roles.add(DataSet.role[2]);
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername(DataSet.username[2]);
        signUpRequest.setEmail(DataSet.email[2]);
        signUpRequest.setPassword(DataSet.password[2]);
        signUpRequest.setAddress(DataSet.address[2]);
        signUpRequest.setRole(roles);

        String body = objectMapper.writeValueAsString(signUpRequest);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.username").value(DataSet.username[2]))
                .andExpect(jsonPath("$.email").value(DataSet.email[2]));

        // Verify the user is saved in the database
        User registeredUser = userRepository.findByUsername(DataSet.username[2]).orElse(null);
        assert registeredUser != null;
        assert passwordEncoder.matches(DataSet.password[2], registeredUser.getPassword());
    }
}