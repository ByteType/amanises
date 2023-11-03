package com.bytetype.amanises.service;

import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.payload.common.CabinetPayload;
import com.bytetype.amanises.payload.request.LockerRequest;
import com.bytetype.amanises.payload.response.LockerResponse;
import com.bytetype.amanises.repository.LockerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class LockerService {
    @Autowired
    private LockerRepository lockerRepository;

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

    public LockerResponse createLocker(LockerRequest request) {
        Locker locker = new Locker();
        locker.setLocation(request.getLocation());
        locker.setSize(request.getSize());

        locker = lockerRepository.save(locker);

        return new LockerResponse(locker.getLocation(), new ArrayList<>());
    }

    public Locker updateLocker(Long id, LockerRequest request) {
        var locker = lockerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locker not found with id: " + id));

        return lockerRepository.save(locker);
    }
}
