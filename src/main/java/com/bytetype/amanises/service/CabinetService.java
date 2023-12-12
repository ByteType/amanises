package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.CabinetNotFoundException;
import com.bytetype.amanises.model.Cabinet;
import com.bytetype.amanises.model.CabinetType;
import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.response.CabinetResponse;
import com.bytetype.amanises.repository.CabinetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CabinetService {

    @Autowired
    private CabinetRepository cabinetRepository;

    public CabinetResponse getCabinetById(Long id) throws CabinetNotFoundException {
        Cabinet cabinet = cabinetRepository.findById(id).orElseThrow(CabinetNotFoundException::new);
        ParcelPayload parcel = Optional.ofNullable(cabinet.getParcel()).map(ParcelPayload::createFrom).orElse(null);
        Long lockerId = Optional.ofNullable(cabinet.getLocker()).map(Locker::getId).orElse(null);

        return new CabinetResponse(cabinet.getId(), cabinet.getType(), parcel, lockerId);
    }

    @Transactional
    public List<CabinetResponse> getFreeCabinets() {
        List<Cabinet> cabinets = cabinetRepository.findByType(CabinetType.OPEN);

        return cabinets.stream()
                .map(cabinet -> new CabinetResponse(
                        cabinet.getId(),
                        cabinet.getType(),
                        Optional.ofNullable(cabinet.getParcel()).map(ParcelPayload::createFrom).orElse(null),
                        Optional.ofNullable(cabinet.getLocker()).map(Locker::getId).orElse(null)
                ))
                .collect(Collectors.toList());
    }
}
