package com.bytetype.amanises;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.*;
import com.bytetype.amanises.payload.common.UserPayload;
import com.bytetype.amanises.payload.request.*;
import com.bytetype.amanises.repository.*;
import com.bytetype.amanises.security.jwt.JwtTokenProvider;
import com.bytetype.amanises.utility.LockerUtilities;
import com.bytetype.amanises.utility.UserUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.bytetype.amanises.utility.ParcelUtilities.generateCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({"test", "non-async"})
public class ParcelControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private ParcelExpectRepository parcelExpectRepository;

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

    private static final String className = ParcelControllerTests.class.getSimpleName();

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "springboot"))
            .withPerMethodLifecycle(false);

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
        parcel.setStatus(ParcelStatus.CREATE);
        parcel.setReadyForPickupAt(LocalDateTime.now());
        parcel.setDeliveryCode(generateCode(4));
        parcel = parcelRepository.saveAndFlush(parcel);

        cabinet.setParcel(parcel);
        cabinetRepository.saveAndFlush(cabinet);

        mockMvc.perform(get("/api/parcels/{id}", parcel.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ParcelStatus.CREATE.name()))
                .andExpect(jsonPath("$.location").isString());
    }

    @Test
    public void testGetParcelsByStatus() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[0]).orElseThrow();
        Random random = new Random();

        Parcel parcel = new Parcel();
        parcel.setSender(sender);
        parcel.setRecipient(recipient);
        parcel.setWidth(random.nextDouble() * 10.0);
        parcel.setHeight(random.nextDouble() * 5.0);
        parcel.setDepth(random.nextDouble() * 2.0);
        parcel.setMass(random.nextDouble() * 1.5);
        parcel.setStatus(ParcelStatus.DISTRIBUTE);
        parcel.setReadyForPickupAt(LocalDateTime.now());
        parcel.setDeliveryCode(generateCode(4));
        parcel = parcelRepository.saveAndFlush(parcel);

        List<ParcelExpect> parcelExpects = new ArrayList<>();

        ParcelExpect parcelExpect = new ParcelExpect();
        parcelExpect.setLocker(locker);
        parcelExpect.setParcel(parcel);
        parcelExpects.add(parcelExpect);

        parcelExpectRepository.saveAll(parcelExpects);

        mockMvc.perform(get("/api/parcels")
                        .param("location", DataSet.location[0])
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$..status").value(ParcelStatus.DISTRIBUTE.name()));
    }

    @Test
    public void testCreateParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername(DataSet.username[0]);
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[0]).orElseThrow();
        Random random = new Random();

        ParcelCreateRequest createRequest = new ParcelCreateRequest();
        createRequest.setSender(UserPayload.createFrom(sender));
        createRequest.setRecipient(UserPayload.createFrom(recipient));
        createRequest.setWidth(random.nextDouble() * 10.0);
        createRequest.setHeight(random.nextDouble() * 5.0);
        createRequest.setDepth(random.nextDouble() * 2.0);
        createRequest.setMass(random.nextDouble() * 1.5);
        createRequest.setReadyForPickupAt(LocalDateTime.now());
        createRequest.setExpectedSenderLockers(List.of(locker.getId()));
        createRequest.setExpectedRecipientLockers(List.of(locker.getId()));

        String body = objectMapper.writeValueAsString(createRequest);

        mockMvc.perform(post("/api/parcels")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ParcelStatus.CREATE.name()))
                .andExpect(jsonPath("$.deliveryCode").isString());
    }

    @Test
    public void testDeliveryParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername(DataSet.username[0]);
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[1]).orElseThrow();
        Cabinet cabinet = cabinetRepository.findEmptyCabinetsByLockerId(locker.getId()).get(0);
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

        List<ParcelExpect> parcelExpects = new ArrayList<>();

        ParcelExpect parcelExpect = new ParcelExpect();
        parcelExpect.setLocker(locker);
        parcelExpect.setParcel(parcel);
        parcelExpects.add(parcelExpect);

        parcelExpectRepository.saveAllAndFlush(parcelExpects);

        cabinet.setParcel(parcel);
        cabinetRepository.saveAndFlush(cabinet);

        ParcelDeliveryRequest deliveryRequest = new ParcelDeliveryRequest();
        deliveryRequest.setLockerId(locker.getId());
        deliveryRequest.setDeliveryCode(parcel.getDeliveryCode());

        String body = objectMapper.writeValueAsString(deliveryRequest);

        mockMvc.perform(post("/api/parcels/delivery")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ParcelStatus.DELIVERED.name()));
    }

    @Test
    public void testDistributeParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[1]).orElseThrow();
        Cabinet cabinet = cabinetRepository.findEmptyCabinetsByLockerId(locker.getId()).get(0);
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

        ParcelDistributeRequest distributeRequest = new ParcelDistributeRequest();
        distributeRequest.setId(parcel.getId());
        distributeRequest.setCabinetId(cabinet.getId());

        String body = objectMapper.writeValueAsString(distributeRequest);

        mockMvc.perform(post("/api/parcels/distribute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ParcelStatus.DISTRIBUTE.name()));
    }

    @Test
    public void testArriveParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername("Driver");
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[1]).orElseThrow();
        Cabinet cabinet = cabinetRepository.findEmptyCabinetsByLockerId(locker.getId()).get(0);
        Random random = new Random();

        Parcel parcel = new Parcel();
        parcel.setSender(sender);
        parcel.setRecipient(recipient);
        parcel.setWidth(random.nextDouble() * 10.0);
        parcel.setHeight(random.nextDouble() * 5.0);
        parcel.setDepth(random.nextDouble() * 2.0);
        parcel.setMass(random.nextDouble() * 1.5);
        parcel.setReadyForPickupAt(LocalDateTime.now());
        parcel = parcelRepository.saveAndFlush(parcel);

        cabinet.setType(CabinetType.OPEN);
        cabinet = cabinetRepository.saveAndFlush(cabinet);

        ParcelArriveRequest arriveRequest = new ParcelArriveRequest();
        arriveRequest.setId(parcel.getId());
        arriveRequest.setCabinetId(cabinet.getId());

        String body = objectMapper.writeValueAsString(arriveRequest);

        mockMvc.perform(post("/api/parcels/arrive")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ParcelStatus.READY_FOR_PICKUP.name()))
                .andExpect(jsonPath("$.pickupCode").isString());

        final MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        final MimeMessage receivedMessage = receivedMessages[0];

        assertEquals(DataSet.email[1], receivedMessage.getAllRecipients()[0].toString());
    }

    @Test
    public void testPickUpParcel() throws Exception {
        String token = "Bearer " + jwtTokenProvider.generateTokenFromUsername(DataSet.username[0]);
        User sender = userRepository.findByUsername(DataSet.username[0]).orElseThrow();
        User recipient = userRepository.findByUsername(DataSet.username[1]).orElseThrow();
        Locker locker = lockerRepository.findByLocationWithCabinets(DataSet.location[1]).orElseThrow();
        Cabinet cabinet = cabinetRepository.findEmptyCabinetsByLockerId(locker.getId()).get(0);
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

        cabinet.setParcel(parcel);
        cabinet.setType(CabinetType.PICKUP_PARCEL_EXIST);
        cabinetRepository.save(cabinet);

        ParcelPickUpRequest pickUpRequest = new ParcelPickUpRequest();
        pickUpRequest.setLockerId(locker.getId());
        pickUpRequest.setPickupCode(code);

        String body = objectMapper.writeValueAsString(pickUpRequest);

        mockMvc.perform(post("/api/parcels/pickup")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ParcelStatus.PICKED_UP.name()));
    }
}
