package com.bytetype.amanises.payload.response;

import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.common.UserPayload;

import java.util.List;
import java.util.Set;

public class UserDetailResponse extends UserPayload {

    private Set<Role> roles;

    private List<ParcelPayload> parcels;

    public UserDetailResponse(
            Long id,
            String username,
            String email,
            String phone,
            String address,
            Set<Role> roles,
            List<ParcelPayload> parcels
    ) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        this.roles = roles;
        this.parcels = parcels;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<ParcelPayload> getParcels() {
        return parcels;
    }

    public void setParcels(List<ParcelPayload> parcels) {
        this.parcels = parcels;
    }
}
