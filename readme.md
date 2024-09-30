# 비트코인 투자 알림 서비스  ![image](https://github.com/user-attachments/assets/af4df438-aba2-4ac4-92e7-1e6214280d5a)



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

업비트 WebSocket API를 사용하여 실시간 데이터를 수신합니다. 브라우저에서 직접 WebSocket 연결을 설정합니다:

```javascript
let socket = new WebSocket('wss://api.upbit.com/websocket/v1');
```

### 데이터 처리

WebSocket으로부터 수신된 데이터는 ArrayBuffer 형식으로 오기 때문에, 이를 적절히 처리하여 JSON으로 변환합니다:

```javascript
socket.onmessage = function(event) {
    if (event.data instanceof ArrayBuffer) {
        var text = new TextDecoder().decode(event.data);
        var jsonData = JSON.parse(text);
        // 데이터 처리 로직
    }
};
```

### 보안 설정

세션 관리를 위해 Spring Security 설정에서 `SessionCreationPolicy.IF_REQUIRED`를 사용합니다.

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

### 세션 관리 문제

**문제**: 뷰 화면에 로그인이 정상적으로 반영되지 않음

**해결**: Spring Security 설정에서 `SessionCreationPolicy.IF_REQUIRED` 사용

```java
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
```

### WebSocket URL 스키마 오류

**문제**: WebSocket URL 사용 시 "The URL's scheme must be either 'http:' or 'https:'. 'wss:' is not allowed" 오류 발생

**해결**: 브라우저에서 직접 WebSocket을 사용하도록 변경

```javascript
let socket = new WebSocket('wss://api.upbit.com/websocket/v1');
```

참고: https://github.com/sockjs/sockjs-client/issues/452

### ArrayBuffer 데이터 처리

**문제**: "Unexpected token 'o', '[object ArrayBuffer]' is not valid JSON" 오류 발생

**해결**: ArrayBuffer 데이터를 텍스트로 변환 후 JSON으로 파싱

```javascript
socket.onmessage = function(event) {
    if (event.data instanceof ArrayBuffer) {
        var text = new TextDecoder().decode(event.data);
        var jsonData = JSON.parse(text);
        if (jsonData.type === 'ticker') {
            var price = jsonData.trade_price;
            document.getElementById("price").innerText = "Current BTC Price: " + price;
        }
        console.log("Received message:", jsonData);
    } else {
        console.log("Received unknown data type");
    }
};
```

**설명**:
1. ArrayBuffer 처리: `instanceof ArrayBuffer`를 사용하여 데이터 타입 확인
2. 텍스트 변환: `TextDecoder`를 사용하여 바이너리 데이터를 텍스트로 변환
3. JSON 파싱: `JSON.parse()`를 사용하여 텍스트를 JSON 객체로 변환
4. 데이터 처리: 변환된 JSON 데이터에서 필요한 정보 추출 및 표시


## 기여 방법

[프로젝트에 기여하는 방법에 대한 설명]

## 라이선스

[프로젝트의 라이선스 정보]
