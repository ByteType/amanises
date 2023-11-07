package com.bytetype.amanises.payload.request;

import java.time.LocalDateTime;

public class ParcelPickUpRequest {
    private Long id;

    private LocalDateTime pickedUpAt; // Require precises timing

    private String pickupCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
