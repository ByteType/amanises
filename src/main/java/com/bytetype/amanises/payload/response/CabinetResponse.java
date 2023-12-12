package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.CabinetType;
import com.bytetype.amanises.payload.common.CabinetPayload;
import com.bytetype.amanises.payload.common.ParcelPayload;

public class CabinetResponse extends CabinetPayload {

    private ParcelPayload parcel;

    private Long lockerId;

    public CabinetResponse(Long id, CabinetType type, ParcelPayload parcel, Long lockerId) {
        this.parcel = parcel;
        this.lockerId = lockerId;
        this.setId(id);
        this.setType(type);
    }

    public ParcelPayload getParcel() {
        return parcel;
    }

    public void setParcel(ParcelPayload parcel) {
        this.parcel = parcel;
    }

    public Long getLockerId() {
        return lockerId;
    }

    public void setLockerId(Long lockerId) {
        this.lockerId = lockerId;
    }
}
