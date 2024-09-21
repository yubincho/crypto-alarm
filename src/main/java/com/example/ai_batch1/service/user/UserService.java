package com.example.ai_batch1.service.user;

import com.example.ai_batch1.exception.ResourceNotFoundException;
import com.example.ai_batch1.domain.user.UserEntity;
import com.example.ai_batch1.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public UserEntity oauthSave(UserEntity entity) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(UserEntity.builder()
                .email(entity.getEmail())
                .password(encoder.encode(entity.getPassword()))
                .build());
    }


    public UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No user found"));
    }

    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No user found"));
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
