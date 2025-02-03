package com.example.websockets.user;


import com.example.websockets.chatroom.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
    }

    public void disconnect(User user) {
        var storedUser = userRepository.findById(user.getNickName()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public String findUserFullnameByNickName(String nickName) {
        return userRepository.findByNickName(nickName).getFullName();
    }

    public List<User> findAllRecipientsOfSender(String nickname) {
        return userRepository.findAllByNickName(nickname);
    }
}
