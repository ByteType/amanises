package com.bytetype.amanises.controller;

import com.bytetype.amanises.payload.request.LoginRequest;
import com.bytetype.amanises.payload.request.SignupRequest;
import com.bytetype.amanises.payload.response.MessageResponse;
import com.bytetype.amanises.payload.response.UserInfoResponse;
import com.bytetype.amanises.security.jwt.JwtTokenProvider;
import com.bytetype.amanises.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserInfoResponse response = authService.authenticateUser(loginRequest);
            response.setToken(jwtTokenProvider.generateTokenFromUsername(response.getUsername()));
            ResponseCookie jwtCookie = jwtTokenProvider.generateJwtCookie(response.getUsername());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            UserInfoResponse response = authService.registerUser(signUpRequest);
            response.setToken(jwtTokenProvider.generateTokenFromUsername(response.getUsername()));
            ResponseCookie jwtCookie = jwtTokenProvider.generateJwtCookie(response.getUsername());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtTokenProvider.getCleanJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Success: You've been signed out!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUser(@PathVariable("id") Long id) {
        ResponseCookie cookie = jwtTokenProvider.getCleanJwtCookie();

        try {
            if (authService.removeUser(id))
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new MessageResponse("Success: You've canceled the account"));
            else
                return ResponseEntity.badRequest()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new MessageResponse("Bad operation: Unsuccessful user query."));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new MessageResponse("Unknown: Unknown Error"));
        }
    }
}
