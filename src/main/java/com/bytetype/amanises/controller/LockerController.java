package com.bytetype.amanises.controller;

import com.bytetype.amanises.payload.common.LockerPayload;
import com.bytetype.amanises.payload.request.LockerRequest;
import com.bytetype.amanises.payload.response.LockerResponse;
import com.bytetype.amanises.payload.response.MessageResponse;
import com.bytetype.amanises.service.LockerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/lockers")
public class LockerController {

    @Autowired
    private LockerService lockerService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllLocker() {
        try {
            List<LockerPayload> response = lockerService.getAllLocker();

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLockerById(@PathVariable Long id) {
        try {
            LockerResponse response = lockerService.getLockerStatusById(id);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getLockerByLocation(@RequestParam String location) {
        try {
            LockerResponse response = lockerService.getLockerStatusByLocation(location);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> createLocker(@Valid @RequestBody LockerRequest request) {
        try {
            LockerResponse response = lockerService.createLocker(request);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }
}
