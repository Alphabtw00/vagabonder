package com.vagabonder.controller;

import com.vagabonder.entity.User;
import com.vagabonder.service.AuthService;
import com.vagabonder.dto.LoginRequestDTO;
import com.vagabonder.dto.RegisterRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest){
        User user = authService.loginUser(loginRequest);
        return ResponseEntity.ok(user.getEmail());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequestDTO){
        User user = authService.registerUser(registerRequestDTO);
        return ResponseEntity.ok(user.getEmail());
    }


}
