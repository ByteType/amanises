package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.common.UserPayload;

public class ParcelArriveResponse extends ParcelPayload {

    private String pickupCode;

    public ParcelArriveResponse(
            Long id,
            UserPayload sender,
            UserPayload recipient,
            ParcelStatus status,
            String pickupCode
    ) {
        this.pickupCode = pickupCode;
        this.setId(id);
        this.setSender(sender);
        this.setRecipient(recipient);
        this.setStatus(status);
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }
}
