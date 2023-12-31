package com.bytetype.amanises.controller;

import com.bytetype.amanises.payload.response.CabinetResponse;
import com.bytetype.amanises.payload.response.MessageResponse;
import com.bytetype.amanises.service.CabinetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cabinets")
public class CabinetController {

    @Autowired
    private CabinetService cabinetService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCabinetById(@PathVariable Long id) {
        try {
            CabinetResponse response = cabinetService.getCabinetById(id);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @GetMapping("/free")
    public ResponseEntity<?> getFreeCabinets() {
        try {
            List<CabinetResponse> response = cabinetService.getFreeCabinets();

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }
}
