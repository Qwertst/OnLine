package ru.hse.online.AuthService.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hse.online.AuthService.model.*;
import ru.hse.online.AuthService.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger;

    public AuthResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).orElse(null) != null) {
            logger.info("User {} already exists", request.getEmail());
            return null;
        }
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwt = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            logger.info("User {} not found", request.getEmail());
            return null;
        }
        var jwt = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .build();
    }
}
