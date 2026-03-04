package com.oficina.sgo.service;

import com.oficina.sgo.dto.request.LoginRequest;
import com.oficina.sgo.dto.response.AuthResponse;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.User;
import com.oficina.sgo.repository.UserRepository;
import com.oficina.sgo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after authentication: " + request.username()));
        return new AuthResponse(token, "Bearer", user.getId(), user.getUsername(), user.getName(), user.getRole().name());
    }
}
