package com.bytetype.amanises;

import com.bytetype.amanises.model.Cabinet;
import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.payload.request.LockerRequest;
import com.bytetype.amanises.repository.CabinetRepository;
import com.bytetype.amanises.repository.LockerRepository;
import com.bytetype.amanises.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LockerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CabinetRepository cabinetRepository;

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        Locker locker = new Locker();
        locker.setLocation("TestLocation");

        lockerRepository.saveAndFlush(locker);

        Cabinet cabinet = new Cabinet();
        cabinet.setLocked(true);
        cabinet.setLocker(locker);

        cabinetRepository.saveAndFlush(cabinet);
    }

    @AfterEach
    public void cleanup() {
        cabinetRepository.deleteAll();
        lockerRepository.deleteAll();
    }

    @Test
    public void testGetLocker() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");
        Locker locker = lockerRepository.findByLocation("TestLocation").orElseThrow();

        mockMvc.perform(get("/api/lockers/{id}", locker.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("TestLocation"))
                .andExpect(jsonPath("$.cabinets[0].id").isNumber())
                .andExpect(jsonPath("$.cabinets[0].locked").isBoolean());
    }

    @Test
    public void testCreateLocker() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");

        LockerRequest lockerRequest = new LockerRequest();
        lockerRequest.setLocation("NewLocation");
        lockerRequest.setSize(10);

        String jsonRequest = objectMapper.writeValueAsString(lockerRequest);

        mockMvc.perform(post("/api/lockers")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location", equalTo("NewLocation")))
                .andExpect(jsonPath("$.cabinets", hasSize(10)));
    }
}
