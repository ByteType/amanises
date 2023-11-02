package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.Parcel;
import com.bytetype.amanises.model.ParcelStatus;

import java.time.LocalDateTime;

public class ParcelResponse {
    private Long id;

    private ParcelUserResponse sender;

    private ParcelUserResponse recipient;

    private Double width;

    private Double height;

    private Double depth;

    private Double mass;

    private LocalDateTime readyForPickupAt;

    private LocalDateTime pickedUpAt;

    private ParcelStatus status;

    private String pickupCode;

    public ParcelResponse(
            Long id,
            ParcelUserResponse sender,
            ParcelUserResponse recipient,
            Double width,
            Double height,
            Double depth,
            Double mass,
            LocalDateTime readyForPickupAt,
            LocalDateTime pickedUpAt,
            ParcelStatus status,
            String pickupCode) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.mass = mass;
        this.readyForPickupAt = readyForPickupAt;
        this.pickedUpAt = pickedUpAt;
        this.status = status;
        this.pickupCode = pickupCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelUserResponse getSender() {
        return sender;
    }

    public void setSender(ParcelUserResponse sender) {
        this.sender = sender;
    }

    public ParcelUserResponse getRecipient() {
        return recipient;
    }

    public void setRecipient(ParcelUserResponse recipient) {
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

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public void setReadyForPickupAt(LocalDateTime readyForPickupAt) {
        this.readyForPickupAt = readyForPickupAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public static ParcelResponse createFrom(Parcel parcel) {
        return new ParcelResponse(
                parcel.getId(),
                ParcelUserResponse.createFrom(parcel.getSender()),
                ParcelUserResponse.createFrom(parcel.getRecipient()),
                parcel.getWidth(),
                parcel.getHeight(),
                parcel.getDepth(),
                parcel.getMass(),
                parcel.getReadyForPickupAt(),
                parcel.getPickedUpAt(),
                parcel.getStatus(),
                parcel.getPickupCode()
        );
    }
}