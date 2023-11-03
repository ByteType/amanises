package com.bytetype.amanises.payload.request;

import java.time.LocalDateTime;

public class ParcelPickUpRequest {
    private LocalDateTime pickedUpAt; // Require precises timing

    private String pickupCode;

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
}
