package com.bytetype.amanises.repository;

import com.bytetype.amanises.model.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CabinetRepository extends JpaRepository<Cabinet, Long>  {
    Optional<Cabinet> findByParcelId(Long parcelId);
}
