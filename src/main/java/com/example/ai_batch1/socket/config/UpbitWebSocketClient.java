package com.example.ai_batch1.socket.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;



@Component
public class UpbitWebSocketClient {

    private static final String UPBIT_WS_URL = "wss://api.upbit.com/websocket/v1";

    @PostConstruct
    public void connectToWebSocket() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        webSocketClient.doHandshake(new UpbitWebSocketHandler(), UPBIT_WS_URL);
    }

    private static class UpbitWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String requestPayload = "[{\"ticket\":\"test\"}, {\"type\":\"ticker\", \"codes\":[\"KRW-BTC\"]}]";
            session.sendMessage(new TextMessage(requestPayload));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String requestPayload = message.getPayload();
//            session.sendMessage(new TextMessage(requestPayload));
            System.out.println("Received message: " + requestPayload);
        }
    }
}
