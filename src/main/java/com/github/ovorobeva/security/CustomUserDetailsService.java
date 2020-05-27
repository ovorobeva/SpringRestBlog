package com.github.ovorobeva.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser currentUser = userRepository.findByUsername(username);
        if (currentUser == null) throw new UsernameNotFoundException("Unknown user: " + username);
        UserDetails user = User.builder()
                .username(currentUser.getUsername())
                .password(currentUser.getPassword())
                .roles(currentUser.getRole())
                .build();
        return user;
    }
}
