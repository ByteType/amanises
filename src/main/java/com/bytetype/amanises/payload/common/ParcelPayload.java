package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.Parcel;
import com.bytetype.amanises.model.ParcelStatus;

public class ParcelPayload {
    private UserPayload sender;

    private UserPayload recipient;

    private Double width;

    private Double height;

    private Double depth;

    private Double mass;

    private ParcelStatus status;

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

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public static ParcelPayload createFrom(Parcel parcel) {
        ParcelPayload payload = new ParcelPayload();
        payload.setSender(UserPayload.createFrom(parcel.getSender()));
        payload.setRecipient(UserPayload.createFrom(parcel.getRecipient()));
        payload.setWidth(parcel.getWidth());
        payload.setHeight(parcel.getHeight());
        payload.setDepth(parcel.getDepth());
        payload.setMass(parcel.getMass());
        payload.setStatus(parcel.getStatus());

        return payload;
    }
}
