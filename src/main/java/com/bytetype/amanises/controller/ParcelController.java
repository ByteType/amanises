package com.bytetype.amanises.controller;

import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.request.*;
import com.bytetype.amanises.payload.response.*;
import com.bytetype.amanises.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> getParcelsByExpectedLocation(@RequestParam("location") String location) {
        try {
            List<ParcelPayload> response = parcelService.getParcelsByExpectedLocation(location);

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

    @PostMapping("/distribute")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> distributeParcel(@Valid @RequestBody ParcelDistributeRequest request) {
        try {
            ParcelDistributeResponse response = parcelService.distributeParcel(request);

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
