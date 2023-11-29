package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelDetailPayload;
import com.bytetype.amanises.payload.common.UserPayload;

public class ParcelDeliveryResponse extends ParcelDetailPayload {

    private Long id;

    private ParcelStatus status;

    public ParcelDeliveryResponse(
            Long id,
            UserPayload sender,
            UserPayload recipient,
            Double width,
            Double height,
            Double depth,
            Double mass,
            ParcelStatus status
    ) {
        this.id = id;
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
}
