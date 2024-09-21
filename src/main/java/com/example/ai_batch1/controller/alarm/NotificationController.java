//package com.example.ai_batch1.controller.alarm;
//
//import com.example.ai_batch1.domain.user.UserEntity;
//import com.example.ai_batch1.domain.user.UserRepository;
//import com.example.ai_batch1.service.crypto.TelegramNotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@RequiredArgsConstructor
//@Controller
//public class NotificationController {
//
//    private final TelegramNotificationService notificationService;
//    private final UserRepository userRepository;
//
//    @GetMapping("/")
//    public String mainPage(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
//                !(authentication.getPrincipal() instanceof String);
//
//        System.out.println("Is logged in: " + isLoggedIn);
//
//        model.addAttribute("isLoggedIn", isLoggedIn);
//
//        if (isLoggedIn && authentication instanceof OAuth2AuthenticationToken) {
//            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
//            String username = oauth2User.getAttribute("name");
//            System.out.println("OAuth2User name: " + username);
//            model.addAttribute("username", username);
//        }
//
//        return "main";
//    }
//
//    // 1. 텔레그램 봇 링크를 제공하는 구독 페이지
//    @GetMapping("/subscribe")
//    public String subscribePage(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                authentication instanceof OAuth2AuthenticationToken) {
//            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
//            String email = oauth2User.getAttribute("email");
//
//            // 이메일을 기반으로 봇 링크 생성
//            String botLink = notificationService.generateBotLink(email);
//            model.addAttribute("botLink", botLink); // 텔레그램 봇 링크를 모델에 추가
//
//            UserEntity user = userRepository.findByEmail(email).orElse(null);
//            if (user != null && user.getTelegramChatId() != null) {
//                System.out.println("[telegramUserId] # server: " + user.getTelegramChatId());
//                model.addAttribute("telegramUserId", user.getTelegramChatId());
//            } else {
//                System.out.println("chatId is null for this user.");
//            }
//        }
//
//        return "subscribe"; // 텔레그램 봇 링크를 제공하는 구독 페이지로 이동
//    }
//
////    // 2. 구독 확인 및 완료 처리
////    @GetMapping("/subscribe/confirmation")
////    public String subscribeConfirmationPage(Model model) {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////        if (authentication != null && authentication.isAuthenticated() &&
////                authentication instanceof OAuth2AuthenticationToken) {
////            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
////            String email = oauth2User.getAttribute("email");
////
////            // 이메일로 사용자 정보 및 chatId 확인
////            UserEntity user = userRepository.findByEmail(email).orElse(null);
////            if (user != null && user.getTelegramChatId() != null) {
////                model.addAttribute("telegramUserId", user.getTelegramChatId());  // chatId가 있는 경우 구독 완료
////            } else {
////                // chatId가 없으면 구독 페이지로 이동하여 텔레그램과 상호작용 유도
////                return "redirect:/subscribe";
////            }
////        }
////        return "subscriptionConfirmation";
////    }
//
//
////    // 3. 구독 완료 요청을 처리
////    @PostMapping("/subscribe")
////    public String subscribe(@RequestParam String telegramUserId) {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////        if (authentication != null && authentication.isAuthenticated() &&
////                authentication instanceof OAuth2AuthenticationToken) {
////            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
////            String email = oauth2User.getAttribute("email");
////
////            // 알림을 활성화하고 chatId와 연동
////            notificationService.subscribeUser(email, telegramUserId);
////        }
////        return "subscriptionConfirmation"; // 구독 완료 후 확인 페이지로 이동
////    }
////
////    // 4. 구독 취소 요청을 처리
////    @PostMapping("/unsubscribe")
////    public String unsubscribe() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////        if (authentication != null && authentication.isAuthenticated() &&
////                authentication instanceof OAuth2AuthenticationToken) {
////            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
////            String email = oauth2User.getAttribute("email");
////            notificationService.unsubscribeUser(email);
////        }
////        return "redirect:/"; // 구독 취소 후 메인 페이지로 리다이렉트
////    }
//}
