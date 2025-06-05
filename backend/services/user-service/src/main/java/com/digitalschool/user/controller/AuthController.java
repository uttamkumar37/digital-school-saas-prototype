package com.digitalschool.user.controller;

import com.digitalschool.user.config.JwtTokenProvider;
import com.digitalschool.user.dto.AuthResponse;
import com.digitalschool.user.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        // For multi-tenant, loginRequest should contain tenantId + username + password

        String combinedUsername = loginRequest.getTenantId() + "|" + loginRequest.getUsername();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        combinedUsername,
                        loginRequest.getPassword()
                )
        );

        // Generate JWT token with tenant info
        String token = tokenProvider.generateToken(loginRequest.getUsername(), loginRequest.getTenantId());
        return new AuthResponse(token);
    }
}
