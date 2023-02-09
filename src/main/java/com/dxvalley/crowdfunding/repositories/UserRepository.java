package com.dxvalley.crowdfunding.repositories;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long>{
    @Query("SELECT u FROM Users u WHERE u.username = :username AND u.isEnabled = :isEnabled")
    Optional<Users> findUserByUsername(String username, Boolean isEnabled);

    Optional<Users> findByUsername(String username);
    @Query("SELECT u FROM Users u WHERE u.userId =:userId AND u.isEnabled = TRUE")
    Optional<Users> findByUserId (Long userId);

    @Query("SELECT u FROM Users u WHERE u.isEnabled = TRUE")
    List<Users> findAll();

    @Transactional
    @Modifying
    @Query("UPDATE Users u SET u.isEnabled = TRUE WHERE u.username = :username")
    int enableUser(String username);
}
