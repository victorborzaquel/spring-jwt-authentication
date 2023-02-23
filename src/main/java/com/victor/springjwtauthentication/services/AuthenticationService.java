package com.victor.springjwtauthentication.services;

import com.victor.springjwtauthentication.interfaces.EmailSender;
import com.victor.springjwtauthentication.models.ConfirmationToken;
import com.victor.springjwtauthentication.models.User;
import com.victor.springjwtauthentication.repositories.UserRepository;
import com.victor.springjwtauthentication.vo.AuthenticationRequest;
import com.victor.springjwtauthentication.vo.AuthenticationResponse;
import com.victor.springjwtauthentication.vo.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.victor.springjwtauthentication.enums.Role.USER;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ConfirmationTokenService confirmationTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;
    private final EmailValidator emailValidator;

    public AuthenticationResponse register(RegisterRequest request) {
        if (!emailValidator.test(request.getEmail())) {
            throw new IllegalStateException("Email not valid");
        }

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken");
        }

        final User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(USER)
                .build();

        repository.save(user);

        final ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        confirmationTokenService.save(confirmationToken);

        emailSender.send(
                request.getEmail(),
                buildEmail(request.getFirstName(), confirmationToken.getToken())
        );

        return AuthenticationResponse.builder()
                .token(confirmationToken.getToken())
                .build();

//        return AuthenticationResponse.builder()
//                .token(jwtService.generateToken(user))
//                .build();
    }

    @Transactional
    public String confirmToken(String token) {
        final ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        final LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        enableUser(confirmationToken.getUser().getEmail());

        return "Confirmed";
    }

    public void enableUser(String email) {
        final User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setEnabled(true);

        repository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        final var token = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        authenticationManager.authenticate(token);

        final User user = repository.findByEmail(request.getEmail()).orElseThrow();

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    private String buildEmail(String name, String token) {
        return "Hello, " + name + ". Thank you for signing up to our application. " +
                "Please click on the below url to activate your account: " +
                "http://localhost:8080/api/v1/auth/confirm?token=" + token;
    }
}
