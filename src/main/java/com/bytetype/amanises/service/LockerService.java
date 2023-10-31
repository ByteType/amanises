package com.bytetype.amanises.service;

import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.repository.LockerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockerService {
    @Autowired
    private LockerRepository lockerRepository;

    public Locker getLockerById(Long id) {
        return lockerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Error: Not found locker with %d", id)));
    }
}
