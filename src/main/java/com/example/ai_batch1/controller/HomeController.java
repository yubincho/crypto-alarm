package com.example.ai_batch1.controller;

import com.example.ai_batch1.bot.TelegramBot;
import com.example.ai_batch1.domain.user.UserEntity;
import com.example.ai_batch1.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final TelegramBot telegramBot;
    private final UserRepository userRepository;


    @GetMapping("/login")
    public String home() {
        return "oauthLogin";
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String);

        System.out.println("Is logged in: " + isLoggedIn);

        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn && authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String username = oauth2User.getAttribute("name");

            // 이메일을 기반으로 봇 링크 생성
            String email = oauth2User.getAttribute("email");
            String botLink = telegramBot.generateBotLink(email);
            model.addAttribute("botLink", botLink); // 텔레그램 봇 링크를 모델에 추가

            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getTelegramChatId() != null) {
                System.out.println("[telegramUserId] # server: " + user.getTelegramChatId());
                model.addAttribute("telegramUserId", user.getTelegramChatId());
            } else {
                System.out.println("chatId is null for this user.");
            }

            System.out.println("OAuth2User name: " + username);
            model.addAttribute("username", username);
        }

        return "main2";
    }
}
