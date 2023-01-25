package com.dxvalley.crowdfunding.repositories;
import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<Users,Long>{

    @Query("SELECT u FROM Users u WHERE u.username = ?1 AND u.isEnabled = TRUE")
    Users findByUsername(String username);

    @Query("SELECT u FROM Users u WHERE u.username = ?1")
    Users findUserByUsername(String username);


    @Query("SELECT u FROM Users u WHERE u.username = ?1")
    Users findUser(String username);

    @Query("SELECT u FROM Users u WHERE u.userId = ?1 AND u.isEnabled = TRUE")
    Users findByUserId (Long userId);

    @Query("SELECT u FROM Users u WHERE u.isEnabled = TRUE")
    List<Users> findAll();

    @Transactional
    @Modifying
    @Query("UPDATE Users u " +
            "SET u.isEnabled = TRUE WHERE u.username = ?1")
    int enableUser(String username);
}
