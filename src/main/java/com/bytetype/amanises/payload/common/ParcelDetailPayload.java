package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.Parcel;

public class ParcelDetailPayload extends ParcelLiaisePayload {

    private Double width;

    private Double height;

    private Double depth;

    private Double mass;

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

    public static ParcelDetailPayload createFrom(Parcel parcel) {
        ParcelDetailPayload payload = new ParcelDetailPayload();
        payload.setSender(UserPayload.createFrom(parcel.getSender()));
        payload.setRecipient(UserPayload.createFrom(parcel.getRecipient()));
        payload.setWidth(parcel.getWidth());
        payload.setHeight(parcel.getHeight());
        payload.setDepth(parcel.getDepth());
        payload.setMass(parcel.getMass());

        return payload;
    }
}
