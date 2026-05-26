package com.insta.userservice.service;
import com.insta.userservice.config.JwtUtil;
import com.insta.userservice.dto.*;
import com.insta.userservice.exception.ResourceNotFoundException;
import com.insta.userservice.model.User;
import com.insta.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered");
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken");
        User user = User.builder()
                .username(req.getUsername()).email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName()).build();
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getId());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("Invalid email or password");
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getId());
    }

    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }

    public UserProfileDto getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }

    public UserProfileDto updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getBio() != null) user.setBio(req.getBio());
        if (req.getProfilePictureUrl() != null) user.setProfilePictureUrl(req.getProfilePictureUrl());
        if (req.getWebsite() != null) user.setWebsite(req.getWebsite());
        return mapToDto(userRepository.save(user));
    }

    public List<UserProfileDto> searchByUsername(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private UserProfileDto mapToDto(User u) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(u.getId()); dto.setUsername(u.getUsername()); dto.setEmail(u.getEmail());
        dto.setFullName(u.getFullName()); dto.setBio(u.getBio());
        dto.setProfilePictureUrl(u.getProfilePictureUrl()); dto.setWebsite(u.getWebsite());
        return dto;
    }
}
