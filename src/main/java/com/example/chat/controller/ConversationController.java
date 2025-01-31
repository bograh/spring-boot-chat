package com.example.chat.controller;

import java.util.List;
import java.util.UUID;

import com.example.chat.dto.ChatMessage;
import com.example.chat.dto.UnseenMessageCountResponse;
import com.example.chat.dto.UserConnection;
import com.example.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping(("/api/conversation"))
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/friends")
    public List<UserConnection> getUserFriends() {
        return conversationService.getUserFriends();
    }

    @GetMapping("/unseenMessages")
    public List<UnseenMessageCountResponse> getUnseenMessageCount() {
        return conversationService.getUnseenMessageCount();
    }

    @GetMapping("/unseenMessages/{fromUserId}")
    public List<ChatMessage> getUnseenMessages(@PathVariable("fromUserId") UUID fromUserId) {
        return conversationService.getUnseenMessages(fromUserId);
    }

    @PutMapping("/setReadMessages")
    public List<ChatMessage> setReadMessages(@RequestBody List<ChatMessage> chatMessages) {
        return conversationService.setReadMessages(chatMessages);
    }
}