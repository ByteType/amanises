package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.CabinetNotFoundException;
import com.bytetype.amanises.model.Cabinet;
import com.bytetype.amanises.model.Parcel;
import com.bytetype.amanises.payload.common.UserPayload;
import com.bytetype.amanises.payload.response.CabinetResponse;
import com.bytetype.amanises.repository.CabinetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CabinetService {

    @Autowired
    private CabinetRepository cabinetRepository;

    public CabinetResponse getCabinetById(Long id) throws CabinetNotFoundException {
        Cabinet cabinet = cabinetRepository.findById(id).orElseThrow(CabinetNotFoundException::new);
        Parcel parcel= cabinet.getParcel();

        return new CabinetResponse(
                cabinet.getId(),
                cabinet.getType(),
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
                parcel.getDeliveryCode()
        );
    }
}
