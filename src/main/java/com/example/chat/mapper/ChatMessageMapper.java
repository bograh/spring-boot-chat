package com.example.chat.mapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.chat.config.UserDetailsImpl;
import com.example.chat.dto.ChatMessage;
import com.example.chat.dto.MessageType;
import com.example.chat.entity.Conversation;
import com.example.chat.entity.User;
import com.example.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatMessageMapper {

    private final UserRepository userRepository;

    public List<ChatMessage> toChatMessages(
            List<Conversation> conversationEntities,
            UserDetailsImpl userDetails,
            MessageType messageType) {
        List<UUID> fromUsersIds =
                conversationEntities.stream().map(Conversation::getFromUser).toList();
        Map<UUID, String> fromUserIdsToUsername =
                userRepository.findAllById(fromUsersIds).stream()
                        .collect(Collectors.toMap(User::getId, User::getUsername));

        return conversationEntities.stream()
                .map(e -> toChatMessage(e, userDetails, fromUserIdsToUsername, messageType))
                .toList();
    }

    private static ChatMessage toChatMessage(
            Conversation e,
            UserDetailsImpl userDetails,
            Map<UUID, String> fromUserIdsToUsername,
            MessageType messageType) {
        return ChatMessage.builder()
                .id(e.getId())
                .messageType(messageType)
                .content(e.getContent())
                .receiverId(e.getToUser())
                .receiverUsername(userDetails.getUsername())
                .senderId(e.getFromUser())
                .senderUsername(fromUserIdsToUsername.get(e.getFromUser()))
                .build();
    }
}