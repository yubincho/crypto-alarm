package com.example.ai_batch1.socket.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/sendMessage")
    @SendTo("/topic/priceAlert")
    public String sendMessage(String message) {
        return "Current price: " + message;  // 메시지를 그대로 반환
    }


}
