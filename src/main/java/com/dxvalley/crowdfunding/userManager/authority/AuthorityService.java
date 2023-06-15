package com.dxvalley.crowdfunding.userManager.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    List<Authority> getAuthorities() {
        return authorityRepository.findAll();

    }
}