package com.example.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.example.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  List<User> findAllByUsernameIn(Set<String> usernames);
}
