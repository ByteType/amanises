package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.*;
import com.bytetype.amanises.model.*;
import com.bytetype.amanises.payload.common.CabinetPayload;
import com.bytetype.amanises.payload.common.UserPayload;
import com.bytetype.amanises.payload.request.ParcelArriveRequest;
import com.bytetype.amanises.payload.request.ParcelCreateRequest;
import com.bytetype.amanises.payload.request.ParcelDeliveryRequest;
import com.bytetype.amanises.payload.request.ParcelPickUpRequest;
import com.bytetype.amanises.payload.response.*;
import com.bytetype.amanises.repository.CabinetRepository;
import com.bytetype.amanises.repository.LockerRepository;
import com.bytetype.amanises.repository.ParcelExpectRepository;
import com.bytetype.amanises.repository.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Retrieves the detailed information of a parcel by its ID, includes cabinet and locker related information.
     *
     * @param id The ID of the parcel to retrieve.
     * @return ParcelDetailResponse containing detailed information about the parcel.
     * @throws ParcelNotFoundException if the parcel with the specified ID is not found.
     */
    public ParcelDetailResponse getParcelById(Long id) throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findById(id).orElseThrow(ParcelNotFoundException::new);
        Cabinet cabinet = cabinetRepository.findByParcelId(id).orElse(null);

        return new ParcelDetailResponse(
                parcel.getId(),
                UserPayload.createFrom(parcel.getSender()),
                UserPayload.createFrom(parcel.getRecipient()),
                parcel.getWidth(),
                parcel.getHeight(),
                parcel.getDepth(),
                parcel.getMass(),
                parcel.getStatus(),
                parcel.getReadyForPickupAt(),
                parcel.getPickedUpAt(),
                parcel.getPickupCode(),
                parcel.getDeliveryCode(),
                cabinet != null ? cabinet.getLocker().getLocation() : null,
                cabinet != null ? CabinetPayload.createFrom(cabinet) : null
        );
    }

    /**
     * Creates a new parcel record based on the provided request details.
     *
     * @param request The request containing the details for creating a new parcel.
     * @return ParcelCreateResponse containing the details of the newly created parcel.
     * @throws RoleNotFoundException if the role is not found during user creation.
     * @throws InvalidSenderException if the sender details are invalid.
     * @throws InvalidRecipientException if the recipient details are invalid.
     * @throws InvalidLockerException if the specified locker is invalid.
     */
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

        for (Long expectLocker : expectedSenderLockers) {
            if (cabinetRepository.existsEmptyCabinetsByLockerId(expectLocker)) {
                Cabinet cabinet = cabinetRepository.findEmptyCabinetsByLockerId(expectLocker).get(0);
                cabinet.setParcel(parcel);
                cabinet.setType(CabinetType.DELIVERY_PARCEL_EXIST);
                cabinetRepository.save(cabinet);
                break;
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

    /**
     * Processes the delivery of a parcel based on the provided request details.
     *
     * @param request The request containing the details for parcel delivery.
     * @return ParcelDeliveryResponse containing the details of the delivered parcel.
     * @throws InvalidDeliveryCodeException if the delivery code is invalid.
     * @throws IncorrectLockerException if the specified locker is incorrect.
     * @throws Exception for other general exceptions.
     */
    public ParcelDeliveryResponse deliveryParcel(ParcelDeliveryRequest request) throws Exception {
        Cabinet cabinet = cabinetRepository.findByDeliveryCode(request.getDeliveryCode()).orElseThrow(InvalidDeliveryCodeException::new);
        Locker locker = cabinet.getLocker();
        Parcel parcel = cabinet.getParcel();

        if (!locker.getId().equals(request.getLockerId()))
            throw new IncorrectLockerException(locker.getLocation());

        if (!parcel.getDeliveryCode().equals(request.getDeliveryCode()))
            throw new InvalidDeliveryCodeException();

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

    /**
     * Updates the status of a parcel to indicate its arrival at a specified cabinet.
     *
     * @param request The request containing the details for the parcel's arrival.
     * @return ParcelArriveResponse containing the updated details of the parcel.
     * @throws ParcelNotFoundException if the parcel with the specified ID is not found.
     * @throws CabinetNotFoundException if the specified cabinet is not found.
     */
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

    /**
     * Handles the pickup of a parcel by updating its status and related details.
     *
     * @param request The request containing the details for picking up a parcel.
     * @return ParcelPickUpResponse containing the details of the picked-up parcel.
     * @throws InvalidPickupCodeException if the pickup code is invalid.
     * @throws IncorrectLockerException if the specified locker is incorrect.
     * @throws Exception for other general exceptions.
     */
    public ParcelPickUpResponse pickUpParcel(ParcelPickUpRequest request) throws Exception {
        Cabinet cabinet = cabinetRepository.findByPickupCode(request.getPickupCode()).orElseThrow(InvalidPickupCodeException::new);
        Locker locker = cabinet.getLocker();
        Parcel parcel = cabinet.getParcel();

        if (!locker.getId().equals(request.getLockerId()))
            throw new IncorrectLockerException(locker.getLocation());

        if (!parcel.getPickupCode().equals(request.getPickupCode()))
            throw new InvalidPickupCodeException();

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

    /**
     * Deletes a parcel record based on the provided ID.
     *
     * @param id The ID of the parcel to delete.
     * @throws RuntimeException if the parcel with the specified ID is not found.
     */
    public void deleteParcel(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));

        parcelRepository.delete(parcel);
    }

    /**
     * Generates a random code of specified length.
     *
     * @param length The length of the code to generate.
     * @return A string representing the generated code.
     * @throws IllegalArgumentException if the length is non-positive.
     */
    public String generateCode(int length) {
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");
        int max = (int)Math.pow(10, length);

        return String.format("%04d", secureRandom.nextInt(max));
    }
}
