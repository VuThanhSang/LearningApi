package com.example.learning_api.service.common;

import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.secutiry.UserPrincipal;
import com.example.learning_api.repository.database.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsImp implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//        return UserPrincipal.builder()
//                .userId(userEntity.getId())
//                .username(userEntity.getEmail())
//                .password(userEntity.getPassword())
//                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name())))
//                .build();
    }
}