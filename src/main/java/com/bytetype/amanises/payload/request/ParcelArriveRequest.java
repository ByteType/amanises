package com.bytetype.amanises.payload.request;

import com.bytetype.amanises.model.Locker;

import java.time.LocalDateTime;

public class ParcelArriveRequest {

    private Long id;

    private LocalDateTime readyForPickupAt; // Time arrive.

    private Locker locker;

    private Long cabinetId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public void setReadyForPickupAt(LocalDateTime readyForPickupAt) {
        this.readyForPickupAt = readyForPickupAt;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public Long getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Long cabinetId) {
        this.cabinetId = cabinetId;
    }
}
