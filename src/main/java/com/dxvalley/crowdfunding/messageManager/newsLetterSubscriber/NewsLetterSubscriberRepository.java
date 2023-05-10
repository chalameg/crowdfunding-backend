package com.dxvalley.crowdfunding.messageManager.newsLetterSubscriber;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsLetterSubscriberRepository extends JpaRepository<NewsLetterSubscriber, Long> {
    Optional<NewsLetterSubscriber> findByEmail(String email);
}
