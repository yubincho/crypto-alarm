package com.example.ai_batch1.bot;

import com.example.ai_batch1.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@RequiredArgsConstructor
@Configuration
@Slf4j
public class TelegramConfig {

    private final UserRepository userRepository;

    @Bean
    public TelegramBot telegramBot(@Value("${telegram.bot.username}") String botName,
                                   @Value("${telegram.bot.token}") String botToken) {
        TelegramBot telegramBot = new TelegramBot(botName, botToken, userRepository);
        try {
            var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Exception during registration telegram api: {}", e.getMessage());
        }
        return telegramBot;
    }
}