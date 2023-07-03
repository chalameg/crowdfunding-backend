package com.dxvalley.crowdfunding.utils;

import com.dxvalley.crowdfunding.exception.customException.UnauthorizedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentLoggedInUser {

    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException("Access denied. Please provide a valid authentication token.");

        return authentication.getName();
    }
}