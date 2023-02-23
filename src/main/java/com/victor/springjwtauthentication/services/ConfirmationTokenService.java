package com.victor.springjwtauthentication.services;

import com.victor.springjwtauthentication.models.ConfirmationToken;
import com.victor.springjwtauthentication.repositories.ConfirmationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository repository;

    public void save(ConfirmationToken token) {
        repository.save(token);
    }

    public ConfirmationToken getToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
    }

    public void setConfirmedAt(String token) {
        getToken(token).setConfirmedAt(LocalDateTime.now());
    }
}
