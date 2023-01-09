package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.jpa.repository.JpaRepository;


import com.dxvalley.crowdfunding.models.Users;

public interface UserRepository extends JpaRepository<Users,Long>{
    Users findByUsername(String username);
    Users findByUserId (Long userId);
}
