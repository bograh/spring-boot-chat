package com.example.websockets.repository;

import com.example.websockets.model.Status;
import com.example.websockets.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository  extends MongoRepository<User, String> {
    List<User> findAllByStatus(Status status);

    User findByNickName(String nickname);
}
