package com.bytetype.amanises.payload.common;

import com.bytetype.amanises.model.Cabinet;
import com.bytetype.amanises.model.CabinetType;

public class CabinetPayload {

    private Long id;

    private CabinetType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CabinetType getType() {
        return type;
    }

    public void setType(CabinetType type) {
        this.type = type;
    }

    public static CabinetPayload createFrom(Cabinet cabinet) {
        CabinetPayload payload = new CabinetPayload();
        payload.setId(cabinet.getId());
        payload.setType(cabinet.getType());

        return payload;
    }
}
