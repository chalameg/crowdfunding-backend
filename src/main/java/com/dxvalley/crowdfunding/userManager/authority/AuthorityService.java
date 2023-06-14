package com.dxvalley.crowdfunding.userManager.authority;

import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dxvalley.crowdfunding.utils.ApplicationConstants.DB_ERROR_MESSAGE;
import static com.dxvalley.crowdfunding.utils.LoggingUtils.logError;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    List<Authority> getAuthorities() {
        try {
            return authorityRepository.findAll();
        } catch (DataAccessException ex) {
            logError(getClass(), "getAuthorities", ex);
            throw new DatabaseAccessException(DB_ERROR_MESSAGE);
        }
    }
}