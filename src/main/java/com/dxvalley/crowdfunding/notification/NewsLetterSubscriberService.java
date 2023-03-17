package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.model.NewsLetterSubscriber;

import java.util.List;

public interface NewsLetterSubscriberService {
    List<NewsLetterSubscriber> getAllSubscribers();

    NewsLetterSubscriber subscribe(String email);

    NewsLetterSubscriber getSubscriberByEmail(String email);
}
