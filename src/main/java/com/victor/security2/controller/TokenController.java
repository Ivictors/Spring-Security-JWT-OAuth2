package com.victor.security2.controller;

import com.victor.security2.dto.LoginRequest;
import com.victor.security2.dto.LoginResponse;
import com.victor.security2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        var response = tokenService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}