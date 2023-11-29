package com.bytetype.amanises;

import com.bytetype.amanises.model.Cabinet;
import com.bytetype.amanises.model.CabinetType;
import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.payload.request.LockerRequest;
import com.bytetype.amanises.repository.CabinetRepository;
import com.bytetype.amanises.repository.LockerRepository;
import com.bytetype.amanises.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
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

    private static class DataSet {
        private static final String[] location = { "testLocation1", "testLocation2" };
    }

    @BeforeAll
    public void init() {
        Locker locker = new Locker();
        locker.setLocation(DataSet.location[0]);

        lockerRepository.saveAndFlush(locker);

        Cabinet cabinet = new Cabinet();
        cabinet.setLocker(locker);
        cabinet.setType(CabinetType.DELIVERY_PARCEL_EXIST);

        cabinetRepository.saveAndFlush(cabinet);
    }

    @Test
    public void testGetLocker() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[0]).orElseThrow();

        mockMvc.perform(get("/api/lockers/{id}", locker.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value(DataSet.location[0]))
                .andExpect(jsonPath("$.cabinets[0].id").isNumber())
                .andExpect(jsonPath("$.cabinets[0].type").value(CabinetType.DELIVERY_PARCEL_EXIST.name()));
    }

    @Test
    public void testGetLockerByLocation() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[0]).orElseThrow();

        mockMvc.perform(get("/api/lockers")
                        .param("location", locker.getLocation())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value(DataSet.location[0]))
                .andExpect(jsonPath("$.cabinets[0].id").isNumber())
                .andExpect(jsonPath("$.cabinets[0].type").value(CabinetType.DELIVERY_PARCEL_EXIST.name()));
    }

    @Test
    public void testCreateLocker() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");

        LockerRequest lockerRequest = new LockerRequest();
        lockerRequest.setLocation(DataSet.location[1]);
        lockerRequest.setSize(10);

        String body = objectMapper.writeValueAsString(lockerRequest);

        mockMvc.perform(post("/api/lockers")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location", equalTo(DataSet.location[1])))
                .andExpect(jsonPath("$.cabinets", hasSize(10)));
    }
}
