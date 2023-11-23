package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelDetailPayload;
import com.bytetype.amanises.payload.common.UserPayload;

public class ParcelArriveResponse extends ParcelDetailPayload {

    private Long id;

    private ParcelStatus status;

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
        this.status = status;
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

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }
}
