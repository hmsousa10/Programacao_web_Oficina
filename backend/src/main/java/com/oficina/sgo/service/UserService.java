package com.oficina.sgo.service;

import com.oficina.sgo.dto.request.CreateUserRequest;
import com.oficina.sgo.dto.response.UserResponse;
import com.oficina.sgo.exception.BusinessException;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.User;
import com.oficina.sgo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse findById(Long id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id)));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists: " + request.username());
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .role(request.role())
                .active(true)
                .build();
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (!user.getUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists: " + request.username());
        }
        user.setUsername(request.username());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setActive(false);
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getName(), user.getEmail(),
                user.getRole().name(), user.isActive());
    }
}
