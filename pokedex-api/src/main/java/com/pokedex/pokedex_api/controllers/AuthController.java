package com.pokedex.pokedex_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.pokedex.pokedex_api.entities.ApiResponse;
import com.pokedex.pokedex_api.entities.dtos.LoginRequest;
import com.pokedex.pokedex_api.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/login", consumes = { "application/xml" }, produces = { "application/json",
            "application/xml"})
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                    UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            ApiResponse<String> response = new ApiResponse<>(true, "Login exitoso", userDetails.getAuthorities().stream().findFirst().get().getAuthority());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Credenciales inválidas", null);
            return ResponseEntity.status(401).body(response);
        }
    }
}
