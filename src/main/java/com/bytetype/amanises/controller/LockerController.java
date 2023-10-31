package com.bytetype.amanises.controller;

import com.bytetype.amanises.model.Locker;
import com.bytetype.amanises.service.LockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lockers")
public class LockerController {
    @Autowired
    private LockerService lockerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getLockerById(@PathVariable Long id) {
        try {
            Locker locker = lockerService.getLockerById(id);
            return ResponseEntity.ok().body(locker);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
