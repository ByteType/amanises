package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.Locker;

public class LockerPayload {

    private Long id;

    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static LockerPayload createFrom(Locker locker) {
        LockerPayload payload = new LockerPayload();
        payload.setId(locker.getId());
        payload.setLocation(locker.getLocation());

        return payload;
    }
}
