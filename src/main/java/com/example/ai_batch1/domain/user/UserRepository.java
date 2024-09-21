package com.example.ai_batch1.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByLoggedInTrue();

    List<UserEntity> findByLoggedInTrueAndNotificationsEnabledTrue();

    List<UserEntity> findByNotificationsEnabledTrueAndConversationStartedTrue();

    UserEntity findByTelegramChatId(String telegramChatId);

    List<UserEntity> findByNotificationsEnabledTrue();

    Optional<UserEntity> findById(Long userId);
}
