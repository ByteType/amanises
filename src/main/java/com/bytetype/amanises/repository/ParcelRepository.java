package com.bytetype.amanises.repository;

import com.bytetype.amanises.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    @Query("SELECT p FROM Parcel p WHERE p.sender.id = :userId OR p.recipient.id = :userId")
    List<Parcel> findBySenderIdOrRecipientId(@Param("userId") Long userId);
}
