package com.dxvalley.crowdfunding.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dxvalley.crowdfunding.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JwtUtils {
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 7 * 60 * 60 * 1000; // one week
    public static String SECRET_KEY;
    private static CustomUserDetailsService customUserDetailsService;

    public JwtUtils(@Value("${SECRET_KEY}") String privateKey, CustomUserDetailsService customUserDetailsService) {
        JwtUtils.SECRET_KEY = privateKey;
        JwtUtils.customUserDetailsService = customUserDetailsService;
    }

    public static String generateAccessToken(User user, HttpServletRequest request) {
        customUserDetailsService.updateLastLogin(user.getUsername());

        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)) // 30 minutes
                .withIssuer(request.getRequestURL().toString())
                .withClaim("authorities", authorities)
                .sign(Algorithm.HMAC256(SECRET_KEY.getBytes()));
    }

    public static String generateRefreshToken(User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .withIssuer(request.getRequestURL().toString())
                .sign(Algorithm.HMAC256(SECRET_KEY.getBytes()));
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }
}

