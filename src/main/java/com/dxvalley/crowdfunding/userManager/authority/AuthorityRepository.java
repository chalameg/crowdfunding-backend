package com.dxvalley.crowdfunding.userManager.authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Short> {
    Optional<Authority> findByName(String username);

}
