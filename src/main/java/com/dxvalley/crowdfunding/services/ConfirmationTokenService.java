package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.models.ConfirmationToken;
import org.springframework.stereotype.Service;


@Service
public interface ConfirmationTokenService {
    public ConfirmationToken saveConfirmationToken(ConfirmationToken token);

    public ConfirmationToken getToken(String token);

}
