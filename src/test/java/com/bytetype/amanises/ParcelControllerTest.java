package com.bytetype.amanises;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.*;
import com.bytetype.amanises.payload.common.UserPayload;
import com.bytetype.amanises.payload.request.ParcelArriveRequest;
import com.bytetype.amanises.payload.request.ParcelDeliveryRequest;
import com.bytetype.amanises.payload.request.ParcelPickUpRequest;
import com.bytetype.amanises.repository.*;
import com.bytetype.amanises.security.jwt.JwtTokenProvider;
import com.bytetype.amanises.utility.LockerUtilities;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.bytetype.amanises.utility.ParcelUtilities.generateCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class ParcelControllerTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    private static final String className = ParcelControllerTest.class.getSimpleName();

    private static class DataSet {
        private static final String[] username = { className + "User1", className + "User2" };

        private static final String[] email = { className +"User1@testDomain.com", className + "User2@testDomain.com" };

        private static final String[] password = { "testPassword1", "testPassword2" };

        private static final String[] address = { "123 Main St, " + className + "Town, NA", "456 Main St, " + className + "Town, NA" };

        private static final String[] location = { "NullTown, NA", "BlobTown, NA" };
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
    public void testDeliveryParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername(DataSet.username[0]);
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[0]).orElseThrow();
        Random random = new Random();

        ParcelDeliveryRequest deliveryRequest = new ParcelDeliveryRequest();
        deliveryRequest.setSender(UserPayload.createFrom(sender));
        deliveryRequest.setRecipient(UserPayload.createFrom(recipient));
        deliveryRequest.setWidth(random.nextDouble() * 10.0);
        deliveryRequest.setHeight(random.nextDouble() * 5.0);
        deliveryRequest.setDepth(random.nextDouble() * 2.0);
        deliveryRequest.setMass(random.nextDouble() * 1.5);
        deliveryRequest.setReadyForPickupAt(LocalDateTime.now());
        deliveryRequest.setExpectedLockerId(List.of(locker.getId()));

        String body = objectMapper.writeValueAsString(deliveryRequest);

        mockMvc.perform(post("/api/parcels/delivery")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deliveryCode").isString());
    }

    @Test
    public void testArriveParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername(DataSet.username[0]);
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Cabinet cabinet = cabinetRepository.findAll().get(0);
        Random random = new Random();

        Parcel parcel = new Parcel();
        parcel.setSender(sender);
        parcel.setRecipient(recipient);
        parcel.setWidth(random.nextDouble() * 10.0);
        parcel.setHeight(random.nextDouble() * 5.0);
        parcel.setDepth(random.nextDouble() * 2.0);
        parcel.setMass(random.nextDouble() * 1.5);
        parcel.setReadyForPickupAt(LocalDateTime.now());
        parcel.setDeliveryCode(generateCode(4));
        parcel = parcelRepository.saveAndFlush(parcel);

        ParcelArriveRequest arriveRequest = new ParcelArriveRequest();
        arriveRequest.setId(parcel.getId());
        arriveRequest.setReadyForPickupAt(LocalDateTime.now());
        arriveRequest.setCabinetId(cabinet.getId());

        String body = objectMapper.writeValueAsString(arriveRequest);

        mockMvc.perform(post("/api/parcels/arrive")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pickupCode").isString());
    }

    @Test
    public void testPickUpParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername(DataSet.username[0]);
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Cabinet cabinet = cabinetRepository.findAll().get(1);
        Random random = new Random();

        String code = generateCode(4);

        Parcel parcel = new Parcel();
        parcel.setSender(sender);
        parcel.setRecipient(recipient);
        parcel.setWidth(random.nextDouble() * 10.0);
        parcel.setHeight(random.nextDouble() * 5.0);
        parcel.setDepth(random.nextDouble() * 2.0);
        parcel.setMass(random.nextDouble() * 1.5);
        parcel.setReadyForPickupAt(LocalDateTime.now());
        parcel.setPickupCode(code);
        parcel = parcelRepository.saveAndFlush(parcel);

        cabinet.setLocked(true);
        cabinet.setParcel(parcel);
        cabinet = cabinetRepository.save(cabinet);

        ParcelPickUpRequest pickUpRequest = new ParcelPickUpRequest();
        pickUpRequest.setId(cabinet.getId());
        pickUpRequest.setPickedUpAt(LocalDateTime.now());
        pickUpRequest.setPickupCode(code);

        String body = objectMapper.writeValueAsString(pickUpRequest);

        mockMvc.perform(post("/api/parcels/pickup")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PICKED_UP"));
    }
}
