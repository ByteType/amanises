package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.InvalidRecipientException;
import com.bytetype.amanises.exception.InvalidSenderException;
import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.model.Parcel;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.payload.request.ParcelDeliveryRequest;
import com.bytetype.amanises.payload.response.ParcelDeliveryResponse;
import com.bytetype.amanises.repository.ParcelRepository;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParcelService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private UserService userService;

    public ParcelDeliveryResponse getParcelById(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));
        return ParcelDeliveryResponse.createFrom(parcel);
    }

    public ParcelDeliveryResponse createParcel(ParcelDeliveryRequest request) throws InvalidSenderException, InvalidRecipientException, RoleNotFoundException {
        User sender = userService.getOrCreateUser(request.getSender());
        if (sender == null) throw new InvalidSenderException();

        User recipient = userService.getOrCreateUser(request.getRecipient());
        if (recipient == null) throw new InvalidRecipientException();

        Parcel parcel = new Parcel();
        parcel.setWidth(request.getWidth());
        parcel.setHeight(request.getHeight());
        parcel.setDepth(request.getDepth());
        parcel.setMass(request.getMass());
        parcel.setStatus(request.getStatus());
        parcel.setReadyForPickupAt(request.getReadyForPickupAt());
        parcel.setDeliveryCode(request.getDeliveryCode());
        parcel.setExpectedLocker(request.getExpectedLocker());

        parcel = parcelRepository.save(parcel);

        return ParcelDeliveryResponse.createFrom(parcel);
    }

    public void deleteParcel(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));

        parcelRepository.delete(parcel);
    }
}
