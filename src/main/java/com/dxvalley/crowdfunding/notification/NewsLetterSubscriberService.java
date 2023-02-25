package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.models.NewsLetterSubscriber;

import java.util.List;

public interface NewsLetterSubscriberService {
    List<NewsLetterSubscriber> getAllSubscribers();
    NewsLetterSubscriber subscribe(Long userId, String email);
}
