//package com.example.ai_batch1.service.crypto;
//
//import com.example.ai_batch1.domain.user.UserEntity;
//import com.example.ai_batch1.domain.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Service
//public class TelegramNotificationService extends TelegramLongPollingBot {
//
//    private final UserRepository userRepository;
//    private final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);
//
//    @Value("${telegram.bot.username}")
//    private String botUsername;
//
//    @Value("${telegram.bot.token}")
//    private String botToken;
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId();
//            System.out.println("[chatId] # 1" + chatId);
//
//            if (messageText.startsWith("/start")) {
//                System.out.println("[chatId] # 2" + chatId);
//                handleStartCommand(chatId, messageText);
//            } else {
//                handleUserCommands(chatId, messageText);
//            }
//        }
//    }
//
//    // '/start' 명령어 처리
//    private void handleStartCommand(long chatId, String messageText) {
//        String[] parts = messageText.split(" ");
//        if (parts.length > 1) {
//            String email = parts[1];
//            UserEntity user = userRepository.findByEmail(email).orElse(null);
//
//            if (user != null) {
//                System.out.println("Saving chatId: " + chatId);
//                user.setTelegramChatId(String.valueOf(chatId));
//                user.setNotificationsEnabled(true);
//                userRepository.save(user);
//
//                sendTextMessage(chatId, "알림이 활성화되었습니다. 감사합니다!");
//            } else {
//                sendTextMessage(chatId, "연결된 계정을 찾을 수 없습니다. 웹사이트에서 알림을 신청해주세요.");
//            }
//        } else {
//            sendTextMessage(chatId, "비트코인 알림 봇에 오신 것을 환영합니다! 웹사이트에서 알림을 신청해주세요.");
//        }
//    }
//
// /** ********************************************************************************** */
//    // 사용자 명령어 처리
//    private void handleUserCommands(long chatId, String messageText) {
//        switch (messageText) {
//            case "/enable_notifications":
//                enableNotifications(chatId);
//                break;
//            case "/disable_notifications":
//                disableNotifications(chatId);
//                break;
//            default:
//                sendTextMessage(chatId, "알 수 없는 명령입니다. /enable_notifications 또는 /disable_notifications를 사용해주세요.");
//        }
//    }
//
//    // 알림 활성화
//    public void enableNotifications(long chatId) {
//        UserEntity user = userRepository.findByTelegramChatId(String.valueOf(chatId));
//        if (user != null) {
//            user.setNotificationsEnabled(true);
//            userRepository.save(user);
//            sendTextMessage(chatId, "알림이 활성화되었습니다.");
//        } else {
//            sendTextMessage(chatId, "사용자를 찾을 수 없습니다. 먼저 계정을 연동해주세요.");
//        }
//    }
//
//    // 알림 비활성화
//    public void disableNotifications(long chatId) {
//        UserEntity user = userRepository.findByTelegramChatId(String.valueOf(chatId));
//        if (user != null) {
//            user.setNotificationsEnabled(false);
//            userRepository.save(user);
//            sendTextMessage(chatId, "알림이 비활성화되었습니다.");
//        } else {
//            sendTextMessage(chatId, "사용자를 찾을 수 없습니다. 먼저 계정을 연동해주세요.");
//        }
//    }
//
//    // 알림 대상 사용자에게 메시지 전송
//    public void sendNotificationToEligibleUsers(String message) {
//        List<UserEntity> eligibleUsers = userRepository.findByNotificationsEnabledTrue();
//        for (UserEntity user : eligibleUsers) {
//            sendTextMessage(Long.parseLong(user.getTelegramChatId()), message);
//        }
//    }
//
//    // 텍스트 메시지 전송
//    public void sendTextMessage(long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            logger.error("텔레그램 메시지 전송 실패: " + e.getMessage());
//        }
//    }
//
//    // 텔레그램 봇 링크 생성 (사용자의 이메일과 연동)
//    public String generateBotLink(String email) {
//        return "https://t.me/" + botUsername + "?start=" + email;
//    }
//
//    // 사용자 등록 및 알림 활성화
//    public void subscribeUser(String email, String telegramUserId) {
//        System.out.println("[telegramUserId] # 1" + telegramUserId);
//        UserEntity user = userRepository.findByEmail(email)
//                .orElseGet(() -> {
//                    UserEntity newUser = new UserEntity();
//                    newUser.setEmail(email);
//                    return newUser;
//                });
//
//        user.setTelegramChatId(telegramUserId);
//        user.setNotificationsEnabled(true);
//        userRepository.save(user);
//        System.out.println("[telegramUserId]" + user.getTelegramChatId());
//        System.out.println("[telegramUserId]" + telegramUserId);
//        sendTextMessage(Long.parseLong(telegramUserId), "알림이 활성화되었습니다.");
//    }
//
//    // 사용자 알림 비활성화
//    public void unsubscribeUser(String email) {
//        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
//        userOptional.ifPresent(user -> {
//            user.setNotificationsEnabled(false);
//            userRepository.save(user);
//
//            if (user.getTelegramChatId() != null) {
//                sendTextMessage(Long.parseLong(user.getTelegramChatId()), "알림이 비활성화되었습니다.");
//            }
//        });
//    }
//}
