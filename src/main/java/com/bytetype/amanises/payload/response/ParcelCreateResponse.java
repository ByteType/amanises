package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelDetailPayload;
import com.bytetype.amanises.payload.common.UserPayload;

import java.time.LocalDateTime;
import java.util.List;

public class ParcelCreateResponse extends ParcelDetailPayload {

    private Long id;

    private ParcelStatus status;

    private LocalDateTime readyForPickupAt;

    private String deliveryCode;

    public ParcelCreateResponse(
            Long id,
            UserPayload sender,
            UserPayload recipient,
            Double width,
            Double height,
            Double depth,
            Double mass,
            ParcelStatus status,
            LocalDateTime readyForPickupAt,
            String deliveryCode
    ) {
        this.id = id;
        this.status = status;
        this.readyForPickupAt = readyForPickupAt;
        this.deliveryCode = deliveryCode;
        this.setSender(sender);
        this.setRecipient(recipient);
        this.setWidth(width);
        this.setHeight(height);
        this.setDepth(depth);
        this.setMass(mass);
    }

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

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public void setReadyForPickupAt(LocalDateTime readyForPickupAt) {
        this.readyForPickupAt = readyForPickupAt;
    }

    public String getDeliveryCode() {
        return deliveryCode;
    }

    public void setDeliveryCode(String deliveryCode) {
        this.deliveryCode = deliveryCode;
    }
}
