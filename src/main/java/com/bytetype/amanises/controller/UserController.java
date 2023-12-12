package com.bytetype.amanises.controller;

import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.payload.common.UserPayload;
import com.bytetype.amanises.payload.response.MessageResponse;
import com.bytetype.amanises.payload.response.UserDetailResponse;
import com.bytetype.amanises.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") Long id) {
        try {
            UserDetailResponse response = userService.getUserById(id);

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestParam("role") String role) {
        try {
            List<UserPayload> response = userService.getAllUsers(RoleType.valueOf(role));

            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }
}
