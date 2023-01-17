package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


import com.dxvalley.crowdfunding.models.Users;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<Users,Long>{
    Users findByUsername(String username);
    Users findByUserId (Long userId);


//    @Query("UPDATE ConfirmationToken c " +
//            "SET u.confirmedAt = ?2 " +
//            "WHERE c.token = ?1")
//    List<Users> findAll();
}
