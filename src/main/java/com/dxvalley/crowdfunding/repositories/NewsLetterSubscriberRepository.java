package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.NewsLetterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsLetterSubscriberRepository extends JpaRepository<NewsLetterSubscriber,Long> {
}
