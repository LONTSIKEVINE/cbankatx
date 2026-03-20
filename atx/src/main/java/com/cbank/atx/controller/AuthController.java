package com.cbank.atx.controller;

import com.cbank.atx.dto.LoginRequest;
import com.cbank.atx.dto.LoginResponse;
import com.cbank.atx.dto.TwoFactorRequest;
import com.cbank.atx.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(request));
    }

    // POST /api/auth/verify-2fa
    @PostMapping("/verify-2fa")
    public ResponseEntity<LoginResponse> verify2FA(
            @RequestBody TwoFactorRequest request) {
        return ResponseEntity.ok(
                authService.verify2FA(request));
    }
}
