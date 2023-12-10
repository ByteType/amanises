package com.bytetype.amanises;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.*;
import com.bytetype.amanises.repository.*;
import com.bytetype.amanises.security.jwt.JwtTokenProvider;
import com.bytetype.amanises.utility.LockerUtilities;
import com.bytetype.amanises.utility.UserUtilities;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.bytetype.amanises.utility.ParcelUtilities.generateCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class CabinetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private CabinetRepository cabinetRepository;

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String className = CabinetControllerTests.class.getSimpleName();

    private static class DataSet {
        private static final String[] username = { className + "User1", className + "User2" };

        private static final String[] email = { className +"User1@testDomain.com", className + "User2@testDomain.com" };

        private static final String[] password = { "testPassword1", "testPassword2" };

        private static final String[] address = { "123 Main St, " + className + "Town, NA", "456 Main St, " + className + "Town, NA" };

        private static final String[] location = { className + "ATown, NA", className + "BTown, NA" };
    }

    @BeforeAll
    public void init() throws RoleNotFoundException {
        Role role = roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(RoleNotFoundException::new);
        LockerUtilities.LockerData lockerData = LockerUtilities.createLockers(Arrays.asList(DataSet.location));
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
        lockerRepository.saveAllAndFlush(lockerData.lockers());
        cabinetRepository.saveAllAndFlush(lockerData.cabinets());
    }

    @Test
    public void testGetParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[0]).orElseThrow();
        Cabinet cabinet = cabinetRepository.findEmptyCabinetsByLockerId(locker.getId()).get(0);
        Random random = new Random();

        Parcel parcel = new Parcel();
        parcel.setSender(sender);
        parcel.setRecipient(recipient);
        parcel.setWidth(random.nextDouble() * 10.0);
        parcel.setHeight(random.nextDouble() * 5.0);
        parcel.setDepth(random.nextDouble() * 2.0);
        parcel.setMass(random.nextDouble() * 1.5);
        parcel.setStatus(ParcelStatus.CREATE);
        parcel.setReadyForPickupAt(LocalDateTime.now());
        parcel.setDeliveryCode(generateCode(4));
        parcel = parcelRepository.saveAndFlush(parcel);

        cabinet.setParcel(parcel);
        cabinetRepository.saveAndFlush(cabinet);

        mockMvc.perform(get("/api/cabinets/{id}", cabinet.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value(CabinetType.OPEN.name()))
                .andExpect(jsonPath("$.status").value(ParcelStatus.CREATE.name()));
    }
}
