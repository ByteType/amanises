package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.Cabinet;

public class CabinetPayload {

    private Long id;

    private Boolean isLocked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public static CabinetPayload createFrom(Cabinet cabinet) {
        CabinetPayload payload = new CabinetPayload();
        payload.setId(cabinet.getId());
        payload.setLocked(cabinet.getLocked());

        return payload;
    }
}
