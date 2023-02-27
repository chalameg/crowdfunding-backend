package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.NewsLetterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsLetterSubscriberRepository extends JpaRepository<NewsLetterSubscriber, Long> {
    Optional<NewsLetterSubscriber> findByEmail(String email);
}
