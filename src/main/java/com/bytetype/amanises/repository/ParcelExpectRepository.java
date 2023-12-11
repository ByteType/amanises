package com.bytetype.amanises.repository;

import com.bytetype.amanises.model.ParcelExpect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParcelExpectRepository extends JpaRepository<ParcelExpect, Long> {

    List<ParcelExpect> findByParcelId(Long parcelId);
}
