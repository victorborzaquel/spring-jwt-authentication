package com.victor.springjwtauthentication.api;

import com.victor.springjwtauthentication.services.AuthenticationService;
import com.victor.springjwtauthentication.vo.AuthenticationRequest;
import com.victor.springjwtauthentication.vo.AuthenticationResponse;
import com.victor.springjwtauthentication.vo.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationResource {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(
            @RequestParam("token") String token
    ) {
        return ResponseEntity.ok(service.confirmToken(token));
    }
}
