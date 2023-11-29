package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.CabinetPayload;
import com.bytetype.amanises.payload.common.ParcelDetailPayload;
import com.bytetype.amanises.payload.common.UserPayload;

import java.time.LocalDateTime;

public class ParcelDetailResponse extends ParcelDetailPayload {

    private Long id;

    private ParcelStatus status;

    private LocalDateTime readyForPickupAt;

    private LocalDateTime pickedUpAt;

    private String pickupCode;

    private String deliveryCode;

    private String location;

    private CabinetPayload cabinet;

    public ParcelDetailResponse(
            Long id,
            UserPayload sender,
            UserPayload recipient,
            Double width,
            Double height,
            Double depth,
            Double mass,
            ParcelStatus status,
            LocalDateTime readyForPickupAt,
            LocalDateTime pickedUpAt,
            String pickupCode,
            String deliveryCode,
            String location,
            CabinetPayload cabinet
    ) {
        this.id = id;
        this.status = status;
        this.readyForPickupAt = readyForPickupAt;
        this.pickedUpAt = pickedUpAt;
        this.pickupCode = pickupCode;
        this.deliveryCode = deliveryCode;
        this.location = location;
        this.cabinet = cabinet;
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

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public String getDeliveryCode() {
        return deliveryCode;
    }

    public void setDeliveryCode(String deliveryCode) {
        this.deliveryCode = deliveryCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CabinetPayload getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetPayload cabinet) {
        this.cabinet = cabinet;
    }
}
