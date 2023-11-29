package com.bytetype.amanises.payload.request;

import com.bytetype.amanises.payload.common.ParcelDetailPayload;

import java.time.LocalDateTime;
import java.util.List;

public class ParcelDeliveryRequest {

    private Long id;

    private String deliveryCode;

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
}
