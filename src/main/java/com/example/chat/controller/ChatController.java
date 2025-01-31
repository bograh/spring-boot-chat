package com.example.chat.controller;

import com.example.chat.dto.ChatMessage;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/sendMessage/{convId}")
    public ChatMessage sendMessageToConvId(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor,
                                           @DestinationVariable("convId") String conversationId) {
        chatService.sendMessageToConvId(chatMessage, conversationId, headerAccessor);
        return chatMessage;
    }
}