package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.Parcel;
import com.bytetype.amanises.model.ParcelStatus;

public class ParcelPayload extends ParcelLiaisePayload {

    private Long id;

    private ParcelStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public static ParcelPayload createFrom(Parcel parcel) {
        ParcelPayload payload = new ParcelPayload();
        payload.setId(parcel.getId());
        payload.setSender(UserPayload.createFrom(parcel.getSender()));
        payload.setRecipient(UserPayload.createFrom(parcel.getRecipient()));
        payload.setStatus(parcel.getStatus());

        return payload;
    }
}
