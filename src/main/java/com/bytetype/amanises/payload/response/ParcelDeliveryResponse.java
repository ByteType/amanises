package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.common.UserPayload;

import java.util.List;

public class ParcelDeliveryResponse extends ParcelPayload {
    private Long id;

    private String deliveryCode;

    private List<Locker> expectedLocker;

    public ParcelDeliveryResponse(
            Long id,
            UserPayload sender,
            UserPayload recipient,
            Double width,
            Double height,
            Double depth,
            Double mass,
            ParcelStatus status,
            String deliveryCode,
            List<Locker> expectedLocker
    ) {
        this.id = id;
        this.deliveryCode = deliveryCode;
        this.expectedLocker = expectedLocker;
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
