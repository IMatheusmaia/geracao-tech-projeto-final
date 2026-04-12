package com.agent.agentApi.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agent.agentApi.rest.auth.JwtService;
import com.agent.agentApi.rest.dto.request.LoginRequest;
import com.agent.agentApi.rest.dto.response.LoginResponse;
import com.agent.agentApi.rest.entity.UserEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserEntity user = (UserEntity) authentication.getPrincipal();

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());

        LoginResponse response = new LoginResponse(token, user.getName(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(response);
    }
}
