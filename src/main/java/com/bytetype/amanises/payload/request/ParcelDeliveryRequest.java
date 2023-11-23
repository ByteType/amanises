package com.bytetype.amanises.payload.request;

import com.bytetype.amanises.payload.common.ParcelDetailPayload;

import java.time.LocalDateTime;
import java.util.List;

public class ParcelDeliveryRequest extends ParcelDetailPayload {

    private LocalDateTime readyForPickupAt; // Expected arrive time.

    private List<Long> expectedLockerId;

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public void setReadyForPickupAt(LocalDateTime readyForPickupAt) {
        this.readyForPickupAt = readyForPickupAt;
    }

    public List<Long> getExpectedLockerId() {
        return expectedLockerId;
    }

    public void setExpectedLockerId(List<Long> expectedLockerId) {
        this.expectedLockerId = expectedLockerId;
    }
}
