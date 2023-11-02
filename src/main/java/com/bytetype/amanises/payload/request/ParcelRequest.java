package com.bytetype.amanises.payload.request;

import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.model.User;

import java.time.LocalDateTime;

public class ParcelRequest {
    private ParcelUserRequest sender;

    private ParcelUserRequest recipient;

    private Double width;

    private Double height;

    private Double depth;

    private Double mass;

    private LocalDateTime readyForPickupAt;

    private LocalDateTime pickedUpAt;

    private ParcelStatus status;

    private String pickupCode;

    public ParcelUserRequest getSender() {
        return sender;
    }

    public void setSender(ParcelUserRequest sender) {
        this.sender = sender;
    }

    public ParcelUserRequest getRecipient() {
        return recipient;
    }

    public void setRecipient(ParcelUserRequest recipient) {
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
}
