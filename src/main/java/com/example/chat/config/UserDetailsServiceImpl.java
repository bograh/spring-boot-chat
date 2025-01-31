package com.example.chat.config;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.chat.entity.User;
import com.example.chat.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Builder
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("user " + username + " not found"));

        Set<GrantedAuthority> authorities =
                userEntity.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                        .collect(Collectors.toSet());

        return UserDetailsImpl.builder()
                .username(userEntity.getUsername())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .password(userEntity.getPassword())
                .authorities(authorities)
                .id(userEntity.getId())
                .build();
    }
}