package com.bytetype.amanises.repository;

import com.bytetype.amanises.model.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CabinetRepository extends JpaRepository<Cabinet, Long>  {

    Optional<Cabinet> findByParcelId(Long parcelId);

    @Query("SELECT c FROM Parcel p JOIN Cabinet c ON p.id = c.parcel.id WHERE p.deliveryCode = :deliveryCode")
    Optional<Cabinet> findByDeliveryCode(String deliveryCode);

    @Query("SELECT c FROM Parcel p JOIN Cabinet c ON p.id = c.parcel.id WHERE p.pickupCode = :pickupCode")
    Optional<Cabinet> findByPickupCode(String pickupCode);

    @Query("SELECT COUNT(c) > 0 FROM Cabinet c WHERE c.locker.id = :lockerId AND c.type = com.bytetype.amanises.model.CabinetType.OPEN")
    boolean existsEmptyCabinetsByLockerId(@Param("lockerId") Long lockerId);

    @Query("SELECT c FROM Cabinet c WHERE c.locker.id = :lockerId AND c.type = com.bytetype.amanises.model.CabinetType.OPEN")
    List<Cabinet> findEmptyCabinetsByLockerId(@Param("lockerId") Long lockerId);
}
