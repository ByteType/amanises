package com.bytetype.amanises.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lockers")
public class Locker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @OneToMany(mappedBy = "locker", cascade = CascadeType.ALL)
    private List<Cabinet> cabinets = new ArrayList<>();

    @OneToMany(mappedBy = "locker")
    private List<ParcelExpect> expectedLocker;

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Cabinet> getCabinets() {
        return cabinets;
    }

    public void setCabinets(List<Cabinet> cabinets) {
        this.cabinets = cabinets;
    }
}
