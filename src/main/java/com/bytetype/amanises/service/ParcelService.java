package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.*;
import com.bytetype.amanises.model.*;
import com.bytetype.amanises.payload.common.UserPayload;
import com.bytetype.amanises.payload.request.ParcelArriveRequest;
import com.bytetype.amanises.payload.request.ParcelCreateRequest;
import com.bytetype.amanises.payload.request.ParcelDeliveryRequest;
import com.bytetype.amanises.payload.request.ParcelPickUpRequest;
import com.bytetype.amanises.payload.response.ParcelArriveResponse;
import com.bytetype.amanises.payload.response.ParcelCreateResponse;
import com.bytetype.amanises.payload.response.ParcelDeliveryResponse;
import com.bytetype.amanises.payload.response.ParcelPickUpResponse;
import com.bytetype.amanises.repository.CabinetRepository;
import com.bytetype.amanises.repository.LockerRepository;
import com.bytetype.amanises.repository.ParcelExpectRepository;
import com.bytetype.amanises.repository.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParcelService {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private CabinetRepository cabinetRepository;

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private ParcelExpectRepository parcelExpectRepository;

    @Autowired
    private UserService userService;

    public Parcel getParcelById(Long id) throws ParcelNotFoundException {
        return parcelRepository.findById(id).orElseThrow(ParcelNotFoundException::new);
    }

    public ParcelCreateResponse createParcel(ParcelCreateRequest request) throws RoleNotFoundException, InvalidSenderException, InvalidRecipientException, InvalidLockerException {
        User sender = userService.getOrCreateUser(request.getSender());
        if (sender == null) throw new InvalidSenderException();

        User recipient = userService.getOrCreateUser(request.getRecipient());
        if (recipient == null) throw new InvalidRecipientException();

        Parcel parcel = new Parcel();
        parcel.setSender(sender);
        parcel.setRecipient(recipient);
        parcel.setWidth(request.getWidth());
        parcel.setHeight(request.getHeight());
        parcel.setDepth(request.getDepth());
        parcel.setMass(request.getMass());
        parcel.setStatus(ParcelStatus.CREATE);
        parcel.setReadyForPickupAt(request.getReadyForPickupAt());
        parcel.setDeliveryCode(generateCode(4));

        parcel = parcelRepository.save(parcel);

        List<Long> expectedSenderLockers = request.getExpectedSenderLockers();
        List<Long> expectedRecipientLockers = request.getExpectedRecipientLockers();

        List<ParcelExpect> parcelExpects = new ArrayList<>();

        if (expectedSenderLockers != null && !expectedSenderLockers.isEmpty()) {
            for (Long expectLocker : expectedSenderLockers) {
                if (cabinetRepository.existsEmptyCabinetsByLockerId(expectLocker)){
                    Cabinet cabinet = cabinetRepository.findEmptyCabinetsByLockerId(expectLocker).get(0);
                    cabinet.setParcel(parcel);
                    cabinet.setType(CabinetType.DELIVERY_PARCEL_EXIST);
                    cabinetRepository.save(cabinet);
                    break;
                }
            }
        }

        if (expectedRecipientLockers != null && !expectedRecipientLockers.isEmpty()) {
            for (Long expectLocker : expectedRecipientLockers) {
                Locker locker = lockerRepository.findById(expectLocker).orElseThrow(InvalidLockerException::new);
                ParcelExpect parcelExpect = new ParcelExpect();
                parcelExpect.setLocker(locker);
                parcelExpect.setParcel(parcel);
                parcelExpects.add(parcelExpect);
            }
        }

        parcelExpectRepository.saveAll(parcelExpects);

        return new ParcelCreateResponse(
                parcel.getId(),
                UserPayload.createFrom(parcel.getSender()),
                UserPayload.createFrom(parcel.getRecipient()),
                parcel.getWidth(),
                parcel.getHeight(),
                parcel.getDepth(),
                parcel.getMass(),
                parcel.getStatus(),
                parcel.getReadyForPickupAt(),
                parcel.getDeliveryCode()
        );
    }

    public ParcelDeliveryResponse deliveryParcel(ParcelDeliveryRequest request) throws Exception {
        Cabinet cabinet = cabinetRepository.findByParcelId(request.getId()).orElseThrow(CabinetNotFoundException::new);
        Parcel parcel = cabinet.getParcel();

        if (!cabinet.getParcel().getDeliveryCode().equals(request.getDeliveryCode()))
            throw new Exception("DeliveryCode incorrect");

        parcel.setStatus(ParcelStatus.DELIVERED);
        parcelRepository.save(parcel);

        cabinet.setParcel(parcel);
        cabinet.setType(CabinetType.DELIVERY_PARCEL_EXIST);
        cabinetRepository.save(cabinet);

        return new ParcelDeliveryResponse(
                parcel.getId(),
                UserPayload.createFrom(parcel.getSender()),
                UserPayload.createFrom(parcel.getRecipient()),
                parcel.getWidth(),
                parcel.getHeight(),
                parcel.getDepth(),
                parcel.getMass(),
                parcel.getStatus()
        );
    }

    public ParcelArriveResponse arriveParcel(ParcelArriveRequest request) throws ParcelNotFoundException, CabinetNotFoundException {
        Cabinet cabinet = cabinetRepository.findById(request.getCabinetId()).orElseThrow(CabinetNotFoundException::new);
        Parcel parcel = parcelRepository.findById(request.getId()).orElseThrow(ParcelNotFoundException::new);

        parcel.setStatus(ParcelStatus.READY_FOR_PICKUP);
        parcel.setReadyForPickupAt(request.getReadyForPickupAt());
        parcel.setPickupCode(generateCode(4));
        parcel.setDeliveryCode(null);
        parcel.setExpectedLocker(null);
        parcelRepository.save(parcel);

        cabinet.setParcel(parcel);
        cabinet.setType(CabinetType.PICKUP_PARCEL_EXIST);
        cabinetRepository.save(cabinet);

        return new ParcelArriveResponse(
                parcel.getId(),
                UserPayload.createFrom(parcel.getSender()),
                UserPayload.createFrom(parcel.getRecipient()),
                parcel.getWidth(),
                parcel.getHeight(),
                parcel.getDepth(),
                parcel.getMass(),
                parcel.getStatus(),
                parcel.getPickupCode()
        );
    }

    public ParcelPickUpResponse pickUpParcel(ParcelPickUpRequest request) throws Exception {
        Cabinet cabinet = cabinetRepository.findByParcelId(request.getId()).orElseThrow(CabinetNotFoundException::new);
        Parcel parcel = cabinet.getParcel();

        if (!cabinet.getParcel().getPickupCode().equals(request.getPickupCode()))
            throw new Exception("PickupCode incorrect");

        parcel.setStatus(ParcelStatus.PICKED_UP);
        parcel.setPickedUpAt(request.getPickedUpAt());
        parcel.setPickupCode(null);
        parcelRepository.save(parcel);

        cabinet.setParcel(null);
        cabinet.setType(CabinetType.OPEN);
        cabinetRepository.save(cabinet);

        return new ParcelPickUpResponse(
                parcel.getId(),
                UserPayload.createFrom(parcel.getSender()),
                UserPayload.createFrom(parcel.getRecipient()),
                parcel.getWidth(),
                parcel.getHeight(),
                parcel.getDepth(),
                parcel.getMass(),
                parcel.getStatus(),
                parcel.getPickedUpAt()
        );
    }

    public void deleteParcel(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));

        parcelRepository.delete(parcel);
    }

    public String generateCode(int length) {
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");
        int max = (int)Math.pow(10, length);

        return String.format("%04d", secureRandom.nextInt(max));
    }
}
