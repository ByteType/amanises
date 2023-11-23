package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.Parcel;

public class ParcelLiaisePayload {

    private UserPayload sender;

    private UserPayload recipient;

    public UserPayload getSender() {
        return sender;
    }

    public void setSender(UserPayload sender) {
        this.sender = sender;
    }

    public UserPayload getRecipient() {
        return recipient;
    }

    public void setRecipient(UserPayload recipient) {
        this.recipient = recipient;
    }

    public static ParcelLiaisePayload createFrom(Parcel parcel) {
        ParcelLiaisePayload payload = new ParcelLiaisePayload();
        payload.setSender(UserPayload.createFrom(parcel.getSender()));
        payload.setRecipient(UserPayload.createFrom(parcel.getRecipient()));

        return payload;
    }
}
