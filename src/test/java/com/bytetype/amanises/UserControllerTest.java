package com.bytetype.amanises;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.repository.ParcelRepository;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import com.bytetype.amanises.security.jwt.JwtTokenProvider;
import com.bytetype.amanises.utility.ParcelUtilities;
import com.bytetype.amanises.utility.UserUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String className = UserControllerTest.class.getSimpleName();

    private static class DataSet {
        private static final String[] username = { className + "User1", className + "User2" };

        private static final String[] email = { className + "User1@testDomain.com", className + "User2@testDomain.com" };

        private static final String[] password = { "testPassword1", "testPassword2" };

        private static final String[] address = { "123 Main St, " + className + "Town, NA", "456 Main St, " + className + "Town, NA" };
    }

    @BeforeAll
    public void init() throws RoleNotFoundException {
        Role role = roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(RoleNotFoundException::new);
        List<User> users = new ArrayList<>();

        for (int i = 0; i < DataSet.username.length; i++) {
            users.add(UserUtilities.createUser(
                    DataSet.username[i],
                    DataSet.email[i],
                    passwordEncoder.encode(DataSet.password[i]),
                    DataSet.address[i],
                    role
            ));
        }

        userRepository.saveAllAndFlush(users);
        parcelRepository.saveAllAndFlush(ParcelUtilities.createParcels(users, 1));
    }

    @Test
    public void testGetUserDetail() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername(DataSet.username[0]);
        User user = userRepository.findByUsername(DataSet.username[0]).orElseThrow();

        mockMvc.perform(get("/api/user/{id}", user.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.parcels", hasSize(2)))
                .andExpect(jsonPath("$.username").value(DataSet.username[0]))
                .andExpect(jsonPath("$.email").value(DataSet.email[0]));
    }
}
