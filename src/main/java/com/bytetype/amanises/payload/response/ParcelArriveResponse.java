package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.common.UserPayload;

public class ParcelArriveResponse extends ParcelPayload {

    private Long id;

    private String pickupCode;

    public ParcelArriveResponse(
            Long id,
            UserPayload sender,
            UserPayload recipient,
            Double width,
            Double height,
            Double depth,
            Double mass,
            ParcelStatus status,
            String pickupCode
    ) {
        this.id = id;
        this.pickupCode = pickupCode;
        this.setSender(sender);
        this.setRecipient(recipient);
        this.setWidth(width);
        this.setHeight(height);
        this.setDepth(depth);
        this.setMass(mass);
        this.setStatus(status);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }
}
