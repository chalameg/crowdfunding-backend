package com.dxvalley.crowdfunding.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.repositories.UserRepository;

import lombok.RequiredArgsConstructor;


@Service @RequiredArgsConstructor
public class CustomUserDeatailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findUserByUsername(username, true).get();
        if (user != null) {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(), user.getPassword(), authorities
            );
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
        }
}
