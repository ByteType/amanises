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

    public ParcelUserRequest getRecipient() {
        return recipient;
    }

    public Double getWidth() {
        return width;
    }

    public Double getHeight() {
        return height;
    }

    public Double getDepth() {
        return depth;
    }

    public Double getMass() {
        return mass;
    }

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public String getPickupCode() {
        return pickupCode;
    }
}
