package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.payload.common.CabinetPayload;

import java.util.List;

public class LockerResponse {

    private String location;

    private List<CabinetPayload> cabinets;

    public LockerResponse(String location, List<CabinetPayload> cabinets) {
        this.location = location;
        this.cabinets = cabinets;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<CabinetPayload> getCabinets() {
        return cabinets;
    }

    public void setCabinets(List<CabinetPayload> cabinets) {
        this.cabinets = cabinets;
    }
}
