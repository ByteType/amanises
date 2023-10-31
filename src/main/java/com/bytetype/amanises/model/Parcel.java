package com.bytetype.amanises.model;

import com.bytetype.amanises.payload.request.ParcelRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parcels")
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    // Object related information
    private Double width;

    private Double height;

    private Double depth;

    private Double mass;

    // Object related information
    private LocalDateTime readyForPickupAt;

    private LocalDateTime pickedUpAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ParcelStatus status;

    private String pickupCode;

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public void setReadyForPickupAt(LocalDateTime readyForPickupAt) {
        this.readyForPickupAt = readyForPickupAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public static Parcel createFrom(ParcelRequest request, User sender, User recipient) {
        Parcel parcel = new Parcel();
        parcel.setSender(sender);
        parcel.setRecipient(recipient);
        parcel.setWidth(request.getWidth());
        parcel.setHeight(request.getHeight());
        parcel.setDepth(request.getDepth());
        parcel.setMass(request.getMass());
        parcel.setReadyForPickupAt(request.getReadyForPickupAt());
        parcel.setPickedUpAt(request.getPickedUpAt());
        parcel.setStatus(request.getStatus());
        parcel.setPickupCode(request.getPickupCode());
        return parcel;
    }
}
