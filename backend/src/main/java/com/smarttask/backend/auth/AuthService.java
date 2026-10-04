package com.smarttask.backend.auth;

import com.smarttask.backend.auth.dto.AuthResponse;
import com.smarttask.backend.auth.dto.LoginRequest;
import com.smarttask.backend.auth.dto.RegisterRequest;
import com.smarttask.backend.security.JwtService;
import com.smarttask.backend.user.Role;
import com.smarttask.backend.user.User;
import com.smarttask.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est deja utilise");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(Role.MEMBER);

        User saved = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(saved);

        return toAuthResponse(saved, accessToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));

        String accessToken = jwtService.generateAccessToken(user);

        return toAuthResponse(user, accessToken);
    }

    public AuthResponse.UserSummary toUserSummary(User user) {
        return new AuthResponse.UserSummary(
            user.getId().toString(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole().name()
        );
    }

    private AuthResponse toAuthResponse(User user, String accessToken) {
        return new AuthResponse(
            accessToken,
            null,
            "Bearer",
            jwtService.getAccessTokenExpirationMs() / 1000,
            toUserSummary(user)
        );
    }
}
