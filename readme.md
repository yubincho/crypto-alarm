# 비트코인 투자 알림 서비스

실시간 비트코인 가격 모니터링 및 투자 알림 서비스입니다.

## 목차
- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [설치 및 실행 방법](#설치-및-실행-방법)
- [기술적 세부사항](#기술적-세부사항)
- [문제 해결 기록](#문제-해결-기록)
- [기여 방법](#기여-방법)
- [라이선스](#라이선스)

## 프로젝트 개요

이 프로젝트는 실시간으로 비트코인 가격을 모니터링하고, 사용자 설정에 따라 투자 알림을 제공하는 서비스입니다. 업비트의 WebSocket API를 활용하여 실시간 데이터를 수집하고, 이를 가공하여 사용자에게 유용한 정보를 제공합니다. 또한, 텔레그램 봇을 통해 즉각적인 알림 서비스를 제공합니다.

## 기술 스택

- Java 17
- Spring Boot 3.2.10
- WebSocket (업비트 Open API)
- OAuth2 (로그인)
- Spring Batch
- Quartz Scheduler
- MySQL
- SockJS
- Telegram Bot API

## 주요 기능

- 실시간 비트코인 가격 모니터링
- 사용자 설정 기반 투자 알림
- OAuth2를 통한 소셜 로그인
- 배치 작업을 통한 주기적 데이터 분석
- 스케줄링된 작업 실행 (Quartz)
- 텔레그램을 통한 실시간 알림 서비스

## 설치 및 실행 방법

[프로젝트 설치 및 실행 방법에 대한 설명]

## 기술적 세부사항

### WebSocket 연결

[기존 내용 유지]

### 데이터 처리

[기존 내용 유지]

### 보안 설정

[기존 내용 유지]

### 텔레그램 봇 연동

텔레그램 봇 API를 사용하여 사용자에게 실시간 알림을 전송합니다:

```java
@Service
public class TelegramNotificationService {

    private final TelegramBot telegramBot;

    public TelegramNotificationService(@Value("${telegram.bot.token}") String botToken) {
        this.telegramBot = new TelegramBot(botToken);
    }

    public void sendAlert(String chatId, String message) {
        SendMessage request = new SendMessage(chatId, message);
        try {
            telegramBot.execute(request);
        } catch (TelegramApiException e) {
            // 예외 처리
        }
    }
}
```

## 문제 해결 기록

[기존 내용 유지]

### 텔레그램 봇 인증 문제

**문제**: 텔레그램 봇 토큰 인증 실패

**해결**: 환경 변수 또는 프로퍼티 파일에서 올바른 봇 토큰을 설정하고, 애플리케이션 시작 시 이를 제대로 로드하는지 확인

```properties
telegram.bot.token=YOUR_BOT_TOKEN_HERE
```

## 기여 방법

[프로젝트에 기여하는 방법에 대한 설명]

## 라이선스

[프로젝트의 라이선스 정보]