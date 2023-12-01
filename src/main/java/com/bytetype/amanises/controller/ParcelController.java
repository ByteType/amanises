package com.bytetype.amanises.controller;

import com.bytetype.amanises.payload.request.ParcelArriveRequest;
import com.bytetype.amanises.payload.request.ParcelCreateRequest;
import com.bytetype.amanises.payload.request.ParcelDeliveryRequest;
import com.bytetype.amanises.payload.request.ParcelPickUpRequest;
import com.bytetype.amanises.payload.response.*;
import com.bytetype.amanises.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getParcelById(@PathVariable(value = "id") Long id) {
        try {
            ParcelDetailResponse response = parcelService.getParcelById(id);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createParcel(@Valid @RequestBody ParcelCreateRequest request) {
        try {
            ParcelCreateResponse response = parcelService.createParcel(request);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping("/delivery")
    public ResponseEntity<?> deliveryParcel(@Valid @RequestBody ParcelDeliveryRequest request) {
        try {
            ParcelDeliveryResponse response = parcelService.deliveryParcel(request);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping("/arrive")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> arriveParcel(@Valid @RequestBody ParcelArriveRequest request) {
        try {
            ParcelArriveResponse response = parcelService.arriveParcel(request);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping("/pickup")
    public ResponseEntity<?> pickupParcel(@Valid @RequestBody ParcelPickUpRequest request) {
        try {
            ParcelPickUpResponse response = parcelService.pickUpParcel(request);

            return ResponseEntity.ok().body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<?> deleteParcel(@PathVariable(value = "id") Long id) {
        parcelService.deleteParcel(id);

        return ResponseEntity.ok().build();
    }
}
