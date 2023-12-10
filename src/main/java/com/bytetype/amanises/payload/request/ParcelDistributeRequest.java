package com.bytetype.amanises.payload.request;

public class ParcelDistributeRequest {

    private Long id;

    private Long cabinetId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Long cabinetId) {
        this.cabinetId = cabinetId;
    }
}
