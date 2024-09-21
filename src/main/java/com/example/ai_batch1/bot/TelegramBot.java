package com.example.ai_batch1.bot;

import com.example.ai_batch1.domain.user.UserEntity;
import com.example.ai_batch1.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final String botName;

    public TelegramBot(String botName, String botToken, UserRepository userRepository) {
        super(botToken);
        this.botName = botName;
        this.userRepository = userRepository;
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            Message message = update.getMessage();
//            var chatId = message.getChatId();
//            log.info("Message received: {}", message.getChatId());
//            var messageText = message.getText();
//            log.info("Message text: {}", messageText);
////            var response = ragAssistant.chat(Math.toIntExact(chatId), messageText);
//            try {
//                execute(new SendMessage(chatId.toString(), "비트코인 알림 봇에 오신 것을 환영합니다! "));
////                execute(new SendMessage(chatId.toString(), response));
//            } catch (TelegramApiException e) {
//                log.error("Exception during processing telegram api: {}", e.getMessage());
//            }
//        }
//    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println("[chatId] # 1" + chatId);

            if (messageText.startsWith("/start")) {
                System.out.println("[chatId] # 2" + chatId);
                handleStartCommand(chatId, messageText);
            } else {
                log.error("Exception during registration telegram api: {}");
            }
        }
    }

    // '/start' 명령어 처리
    private void handleStartCommand(long chatId, String messageText) {
        String[] parts = messageText.split(" ");
        System.out.println("[parts]: " + Arrays.toString(parts));
        if (parts.length >= 2) {
            String email = parts[1].trim();
            UserEntity user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                System.out.println("Saving chatId: " + chatId);
                user.setTelegramChatId(String.valueOf(chatId));
                user.setNotificationsEnabled(true);
                userRepository.save(user);

                sendTextMessage(chatId, "알림이 활성화되었습니다. 감사합니다!");
            } else {
                sendTextMessage(chatId, "연결된 계정을 찾을 수 없습니다. 다시 로그인을 해주세요.");
            }
        } else {
            sendTextMessage(chatId, "비트코인 알림 봇에 오신 것을 환영합니다! \n" +
                    "알림을 받으시려면 이메일 정보를 포함하여 여기에 '/start 이메일' 명령어를 입력해주세요.  \n" +
                    "((예시)) /start example@example.com (띄어쓰기 주의!)");
        }
    }


    // 텍스트 메시지 전송
    public void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("텔레그램 메시지 전송 실패: " + e.getMessage());
        }
    }

    //    // 텔레그램 봇 링크 생성 (사용자의 이메일과 연동)
    public String generateBotLink(String email) {
        return "https://t.me/" + botName + "?start=" + email;
    }

    // 알림 대상 사용자에게 메시지 전송
    public void sendNotificationToEligibleUsers(String message) {
        List<UserEntity> eligibleUsers = userRepository.findByNotificationsEnabledTrue();
        for (UserEntity user : eligibleUsers) {
            sendTextMessage(Long.parseLong(user.getTelegramChatId()), message);
        }
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }
}
