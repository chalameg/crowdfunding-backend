package com.dxvalley.crowdfunding.repository;

import com.dxvalley.crowdfunding.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

}
