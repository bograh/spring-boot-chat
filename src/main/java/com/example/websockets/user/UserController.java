package com.example.websockets.user;

import com.example.websockets.chatroom.ChatRoom;
import com.example.websockets.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public User addUser(
            @Payload User user
    ) {
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public User disconnectUser(
            @Payload User user
    ) {
        userService.disconnect(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    @GetMapping("/chatroom")
    public ResponseEntity<List<HashMap<String, String>>> findChatroomUsers(@RequestParam String nickname) {
        List<HashMap<String, String>> response = new ArrayList<>();
        List<String> recipients = chatRoomService.findAllPreviousUsers(nickname);
        for (String recipient : recipients) {
            HashMap<String, String> user = new HashMap<>();
            user.put("nickName", recipient);
            user.put("fullName", userService.findUserFullnameByNickName(recipient));
            response.add(user);
        }
        return ResponseEntity.ok().body(response);
    }
}
