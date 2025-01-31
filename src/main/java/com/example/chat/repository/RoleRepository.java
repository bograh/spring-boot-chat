package com.example.chat.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.chat.dto.ERole;
import com.example.chat.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
  Optional<Role> findByName(ERole name);
}
