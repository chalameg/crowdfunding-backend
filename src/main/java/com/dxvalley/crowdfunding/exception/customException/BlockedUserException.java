package com.dxvalley.crowdfunding.exception.customException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class BlockedUserException extends UsernameNotFoundException {
    public BlockedUserException(String message) {
        super(message);
    }
}
