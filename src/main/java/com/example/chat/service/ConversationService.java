package com.example.chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.chat.config.UserDetailsImpl;
import com.example.chat.dto.*;
import com.example.chat.entity.Conversation;
import com.example.chat.entity.User;
import com.example.chat.mapper.ChatMessageMapper;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.repository.UserRepository;
import com.example.chat.exception.EntityException;
import com.example.chat.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConversationService {
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final ChatMessageMapper chatMessageMapper;
    private final ConversationRepository conversationRepository;
    private final OnlineOfflineService onlineOfflineService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    public List<UserConnection> getUserFriends() {
        UserDetailsImpl userDetails = securityUtils.getUser();
        String username = userDetails.getUsername();
        List<User> users = userRepository.findAll();
        User thisUser =
                users.stream()
                        .filter(user -> user.getUsername().equals(username))
                        .findFirst()
                        .orElseThrow(EntityException::new);

        return users.stream()
                .filter(user -> !user.getUsername().equals(username))
                .map(
                        user ->
                                UserConnection.builder()
                                        .connectionId(user.getId())
                                        .connectionUsername(user.getUsername())
                                        .convId(getConvId(user, thisUser))
                                        .unSeen(0)
                                        .isOnline(onlineOfflineService.isUserOnline(user.getId()))
                                        .build())
                .toList();
    }

    public List<UnseenMessageCountResponse> getUnseenMessageCount() {
        List<UnseenMessageCountResponse> result = new ArrayList<>();
        UserDetailsImpl userDetails = securityUtils.getUser();
        List<Conversation> unseenMessages =
                conversationRepository.findUnseenMessagesCount(userDetails.getId());

        if (!CollectionUtils.isEmpty(unseenMessages)) {
            Map<UUID, List<Conversation>> unseenMessageCountByUser = new HashMap<>();
            for (Conversation entity : unseenMessages) {
                List<Conversation> values =
                        unseenMessageCountByUser.getOrDefault(entity.getFromUser(), new ArrayList<>());
                values.add(entity);
                unseenMessageCountByUser.put(entity.getFromUser(), values);
            }
            log.info("there are some unseen messages for {}", userDetails.getUsername());
            unseenMessageCountByUser.forEach(
                    (user, entities) -> {
                        result.add(
                                UnseenMessageCountResponse.builder()
                                        .count((long) entities.size())
                                        .fromUser(user)
                                        .build());
                        updateMessageDelivery(user, entities, MessageDeliveryStatusEnum.DELIVERED);
                    });
        }
        return result;
    }

    public List<ChatMessage> getUnseenMessages(UUID fromUserId) {
        List<ChatMessage> result = new ArrayList<>();
        UserDetailsImpl userDetails = securityUtils.getUser();
        List<Conversation> unseenMessages =
                conversationRepository.findUnseenMessages(userDetails.getId(), fromUserId);

        if (!CollectionUtils.isEmpty(unseenMessages)) {
            log.info(
                    "there are some unseen messages for {} from {}", userDetails.getUsername(), fromUserId);
            updateMessageDelivery(fromUserId, unseenMessages, MessageDeliveryStatusEnum.SEEN);
            result = chatMessageMapper.toChatMessages(unseenMessages, userDetails, MessageType.UNSEEN);
        }
        return result;
    }

    private void updateMessageDelivery(
            UUID user,
            List<Conversation> entities,
            MessageDeliveryStatusEnum messageDeliveryStatusEnum) {
        entities.forEach(e -> e.setDeliveryStatus(messageDeliveryStatusEnum.toString()));
        onlineOfflineService.notifySender(user, entities, messageDeliveryStatusEnum);
        conversationRepository.saveAll(entities);
    }

    public List<ChatMessage> setReadMessages(List<ChatMessage> chatMessages) {
        List<UUID> inTransitMessageIds = chatMessages.stream().map(ChatMessage::getId).toList();
        List<Conversation> conversationEntities =
                conversationRepository.findAllById(inTransitMessageIds);
        conversationEntities.forEach(
                message -> message.setDeliveryStatus(MessageDeliveryStatusEnum.SEEN.toString()));
        List<Conversation> saved = conversationRepository.saveAll(conversationEntities);

        return chatMessageMapper.toChatMessages(saved, securityUtils.getUser(), MessageType.CHAT);
    }
}