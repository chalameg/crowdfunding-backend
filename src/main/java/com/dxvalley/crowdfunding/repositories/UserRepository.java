package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dxvalley.crowdfunding.models.Users;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findUsersByUsernameAndIsEnabled(String username, Boolean isEnabled);

    Optional<Users> findByUsername(String username);

    @Query("SELECT u FROM Users u WHERE u.userId =:userId AND u.isEnabled = TRUE")
    Optional<Users> findByUserId(Long userId);

    @Query("SELECT u FROM Users u WHERE u.isEnabled = TRUE")
    List<Users> findAll();

}
