package com.dxvalley.crowdfunding.userManager.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authorities")
@RequiredArgsConstructor
public class AuthorityController {
    private final AuthorityService authorityService;

    @GetMapping
    public ResponseEntity<List<Authority>> getAuthorities() {
        return ResponseEntity.ok(authorityService.getAuthorities());
    }

}
