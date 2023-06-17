package com.dxvalley.crowdfunding.utils;

import com.dxvalley.crowdfunding.exception.customException.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentLoggedInUser {

    // Retrieves the name of the currently logged-in user.
    public String getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException("You do not have the necessary authorization to perform this action.");

        return authentication.getName();
    }
}