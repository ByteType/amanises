package com.bytetype.amanises.controller;

import com.bytetype.amanises.payload.request.ParcelDeliveryRequest;
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
        try {
            return ResponseEntity.ok().body(parcelService.getParcelById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createParcel(@RequestBody ParcelDeliveryRequest request) {
        try {
            return ResponseEntity.ok().body(parcelService.createParcel(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateParcel(@PathVariable(value = "id") Long id, @RequestBody ParcelPickUpRequest request) {
//        try {
//            return ResponseEntity.ok().body(parcelService.updateParcel(id, request));
//        } catch (RuntimeException | InvalidRecipientException | InvalidSenderException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParcel(@PathVariable(value = "id") Long id) {
        parcelService.deleteParcel(id);

        return ResponseEntity.ok().build();
    }
}
