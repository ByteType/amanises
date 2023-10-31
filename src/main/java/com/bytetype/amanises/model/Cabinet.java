package com.bytetype.amanises.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cabinets")
public class Cabinet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locker_id")
    private Locker locker;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    private Boolean isLocked = true;

    public Long getId() {
        return id;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }
}
