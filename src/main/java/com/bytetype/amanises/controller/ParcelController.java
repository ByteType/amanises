package com.bytetype.amanises.controller;

import com.bytetype.amanises.exception.InvalidRecipientException;
import com.bytetype.amanises.exception.InvalidSenderException;
import com.bytetype.amanises.payload.request.ParcelRequest;
import com.bytetype.amanises.payload.response.ParcelResponse;
import com.bytetype.amanises.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {
    @Autowired
    private ParcelService parcelService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getParcelById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok().body(parcelService.getParcelById(id));
    }

    @PostMapping
    public ResponseEntity<?> createParcel(@RequestBody ParcelRequest request) {
        try {
            return ResponseEntity.ok().body(parcelService.createParcel(request));
        } catch (RuntimeException | InvalidRecipientException | InvalidSenderException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateParcel(@PathVariable(value = "id") Long id, @RequestBody ParcelRequest request) {
        try {
            return ResponseEntity.ok().body(parcelService.updateParcel(id, request));
        } catch (RuntimeException | InvalidRecipientException | InvalidSenderException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParcel(@PathVariable(value = "id") Long id) {
        parcelService.deleteParcel(id);

        return ResponseEntity.ok().build();
    }
}
