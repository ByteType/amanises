package com.bytetype.amanises.service;

import com.bytetype.amanises.model.Cabinet;
import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.payload.common.CabinetPayload;
import com.bytetype.amanises.payload.request.LockerRequest;
import com.bytetype.amanises.payload.response.LockerResponse;
import com.bytetype.amanises.repository.CabinetRepository;
import com.bytetype.amanises.repository.LockerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LockerService {

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private CabinetRepository cabinetRepository;

    public LockerResponse getLockerStatusById(Long id) {
        Locker locker = lockerRepository.findByIdWithCabinets(id)
                .orElseThrow(() -> new RuntimeException(String.format("Error: Not found locker with %d", id)));

        return new LockerResponse(
                locker.getLocation(),
                locker.getCabinets().stream()
                        .map(CabinetPayload::createFrom)
                        .collect(Collectors.toList())
        );
    }

    public LockerResponse getLockerStatusByLocation(String location) {
        Locker locker = lockerRepository.findByLocationWithCabinets(location)
                .orElseThrow(() -> new RuntimeException(String.format("Error: Not found locker with %s", location)));

        return new LockerResponse(
                locker.getLocation(),
                locker.getCabinets().stream()
                        .map(CabinetPayload::createFrom)
                        .collect(Collectors.toList())
        );
    }

    public LockerResponse createLocker(LockerRequest request) {
        Locker locker = new Locker();
        locker.setLocation(request.getLocation());
        locker = lockerRepository.save(locker);

        List<Cabinet> cabinets = new ArrayList<>();
        for (int i = 0; i < request.getSize(); i++) {
            Cabinet cabinet = new Cabinet();
            cabinet.setLocker(locker);
            cabinet.setLocked(false);
            cabinets.add(cabinet);
        }

        cabinets = cabinetRepository.saveAll(cabinets);

        return new LockerResponse(
                locker.getLocation(),
                cabinets.stream()
                        .map(CabinetPayload::createFrom)
                        .collect(Collectors.toList())
        );
    }
}
