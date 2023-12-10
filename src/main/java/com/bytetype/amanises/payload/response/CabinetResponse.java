package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.CabinetType;
import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelDetailPayload;
import com.bytetype.amanises.payload.common.UserPayload;

import java.time.LocalDateTime;

public class CabinetResponse extends ParcelDetailPayload {

    private Long id;

    private CabinetType type;

    private Long parcelId;

    private ParcelStatus status;

    private LocalDateTime readyForPickupAt;

    private LocalDateTime pickedUpAt;

    private String pickupCode;

    private String deliveryCode;

    public CabinetResponse(
            Long id,
            CabinetType type,
            Long parcelId,
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
            String deliveryCode
    ) {
        this.id = id;
        this.type = type;
        this.parcelId = parcelId;
        this.status = status;
        this.readyForPickupAt = readyForPickupAt;
        this.pickedUpAt = pickedUpAt;
        this.pickupCode = pickupCode;
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

    public CabinetType getType() {
        return type;
    }

    public void setType(CabinetType type) {
        this.type = type;
    }

    public Long getParcelId() {
        return parcelId;
    }

    public void setParcelId(Long parcelId) {
        this.parcelId = parcelId;
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
}
