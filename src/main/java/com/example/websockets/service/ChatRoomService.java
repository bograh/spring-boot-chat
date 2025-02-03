package com.example.websockets.service;

import com.example.websockets.model.ChatRoom;
import com.example.websockets.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    public List<HashMap<String, String>> findAllPreviousUsers(String nickname) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySenderId(nickname);
        Set<String> recipientIds = new HashSet<>();
        for (ChatRoom chatRoom : chatRooms) {
            recipientIds.add(chatRoom.getRecipientId());
        }
        List<String> recipients = new ArrayList<>(recipientIds);
        List<HashMap<String, String>> response = new ArrayList<>();
        for (String recipient : recipients) {
            HashMap<String, String> user = new HashMap<>();
            user.put("nickName", recipient);
            user.put("fullName", userService.findUserByNickName(recipient).getFullName());
            user.put("status", userService.findUserByNickName(recipient).getStatus().toString());
            response.add(user);
        }
        return response;
    }

    public Optional<String> getChatRoomId(
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists
    ) {
        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }
}
