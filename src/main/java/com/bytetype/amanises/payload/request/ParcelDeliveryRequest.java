package com.bytetype.amanises.payload.request;

import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.payload.common.ParcelPayload;

import java.time.LocalDateTime;
import java.util.List;

public class ParcelDeliveryRequest extends ParcelPayload {
    private LocalDateTime readyForPickupAt;

    private String deliveryCode;

    private List<Locker> expectedLocker;

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

    public List<Locker> getExpectedLocker() {
        return expectedLocker;
    }

    public void setExpectedLocker(List<Locker> expectedLocker) {
        this.expectedLocker = expectedLocker;
    }
}
