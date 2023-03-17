package com.dxvalley.crowdfunding.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtUtils {
    public static String PRIVATE_KEY;

    public JwtUtils(@Value("${PRIVATE_KEY}") String privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("PRIVATE_KEY cannot be null or empty.");
        }
        JwtUtils.PRIVATE_KEY = privateKey;
    }


    public static String create_access_token(User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 3o minutes
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(Algorithm.HMAC256(PRIVATE_KEY.getBytes()));
    }

    public static String create_refresh_token(User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 7 * 60 * 60 * 1000)) // one week
                .withIssuer(request.getRequestURL().toString())
                .sign(Algorithm.HMAC256(PRIVATE_KEY.getBytes()));
    }
}

