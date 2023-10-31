package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.InvalidRecipientException;
import com.bytetype.amanises.exception.InvalidSenderException;
import com.bytetype.amanises.model.Parcel;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.payload.request.ParcelRequest;
import com.bytetype.amanises.payload.request.ParcelUserRequest;
import com.bytetype.amanises.payload.response.ParcelResponse;
import com.bytetype.amanises.repository.ParcelRepository;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ParcelService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    public ParcelResponse getParcelById(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));
        return ParcelResponse.createFrom(parcel);
    }

    public ParcelResponse createParcel(ParcelRequest request) throws InvalidSenderException, InvalidRecipientException {
        User sender = getOrCreateUser(request.getSender());
        if (sender == null) throw new InvalidSenderException();

        User recipient = getOrCreateUser(request.getRecipient());
        if (recipient == null) throw new InvalidRecipientException();

        Parcel savedParcel = parcelRepository.save(Parcel.createFrom(request, sender, recipient));

        return ParcelResponse.createFrom(savedParcel);
    }

    public ParcelResponse updateParcel(Long id, ParcelRequest request) throws InvalidSenderException, InvalidRecipientException {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));

        User sender = getOrCreateUser(request.getSender());
        if (sender == null) throw new InvalidSenderException();
        if (parcel.getSender() != sender) parcel.setSender(sender);

        User recipient = getOrCreateUser(request.getRecipient());
        if (recipient == null) throw new InvalidRecipientException();
        if (parcel.getRecipient() != recipient) parcel.setRecipient(recipient);

        parcel.setWidth(request.getWidth());
        parcel.setHeight(request.getHeight());
        parcel.setDepth(request.getDepth());
        parcel.setMass(request.getMass());
        parcel.setReadyForPickupAt(request.getReadyForPickupAt());
        parcel.setPickedUpAt(request.getPickedUpAt());
        parcel.setStatus(request.getStatus());
        parcel.setPickupCode(request.getPickupCode());

        parcel = parcelRepository.save(parcel);

        return ParcelResponse.createFrom(parcel);
    }

    public void deleteParcel(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));

        parcelRepository.delete(parcel);
    }

    private User getOrCreateUser(ParcelUserRequest request) {
        if (request.getId() != null) {
            User user = userRepository.findById(request.getId()).orElse(null);
            if (user != null) return user;
        }
        if (request.getAddress() != null) {
            User user = userRepository.findByAddress(request.getAddress()).orElse(null);
            if (user != null) return user;

            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.GUEST)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));

            user = User.createFrom(request, roles);
            user.setPassword(null);

            return userRepository.save(user);
        }

        return null;
    }
}
