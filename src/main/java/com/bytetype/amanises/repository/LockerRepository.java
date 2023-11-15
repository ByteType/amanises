package com.bytetype.amanises.repository;

import com.bytetype.amanises.model.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LockerRepository extends JpaRepository<Locker, Long> {

    @Query("SELECT l FROM Locker l LEFT JOIN FETCH l.cabinets WHERE l.id = :id")
    Optional<Locker> findByIdWithCabinets(@Param("id") Long id);

    @Query("SELECT l FROM Locker l LEFT JOIN FETCH l.cabinets WHERE l.location = :location")
    Optional<Locker> findByLocationWithCabinets(@Param("location") String location);
}