package com.bytetype.amanises.controller;

import com.bytetype.amanises.payload.request.LockerRequest;
import com.bytetype.amanises.payload.response.LockerResponse;
import com.bytetype.amanises.service.LockerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lockers")
public class LockerController {
    @Autowired
    private LockerService lockerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getLockerById(@PathVariable Long id) {
        try {
            LockerResponse response = lockerService.getLockerStatusById(id);

            return ResponseEntity.ok().body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createLocker(@Valid @RequestBody LockerRequest request) {
        try {
            LockerResponse response = lockerService.createLocker(request);

            return ResponseEntity.ok().body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
