package com.example.ai_batch1.service.user;

import com.example.ai_batch1.exception.ResourceNotFoundException;
import com.example.ai_batch1.domain.user.UserEntity;
import com.example.ai_batch1.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public UserEntity oauthSave(UserEntity entity) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

//        return userRepository.save(UserEntity.builder()
//                .email(entity.getEmail())
//                .password(encoder.encode(entity.getPassword()))
//                .build());
// -> builder()로 새로 만들면서 email과 password만 넣고 있어. OAuth2SuccessHandler에서 세팅한 nickname이랑 roles가 다 날아가는 문제 발생

        // OAuth 사용자는 랜덤 패스워드가 들어가고, nickname이랑 roles도 그대로 유지
        if (entity.getPassword() == null) {
            entity.setPassword(encoder.encode(UUID.randomUUID().toString()));
        } else {
            entity.setPassword(encoder.encode(entity.getPassword()));
        }

        return userRepository.save(entity);
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
