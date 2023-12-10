package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.common.UserPayload;

public class ParcelDistributeResponse extends ParcelPayload {

    private Long cabinetId;

    public ParcelDistributeResponse(
            Long id,
            Long cabinetId,
            UserPayload sender,
            UserPayload recipient,
            ParcelStatus status
    ) {
        this.cabinetId = cabinetId;
        this.setId(id);
        this.setSender(sender);
        this.setRecipient(recipient);
        this.setStatus(status);
    }

    public Long getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Long cabinetId) {
        this.cabinetId = cabinetId;
    }
}
