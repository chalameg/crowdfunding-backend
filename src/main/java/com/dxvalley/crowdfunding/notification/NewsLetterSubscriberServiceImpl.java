package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.NewsLetterSubscriber;
import com.dxvalley.crowdfunding.repositories.NewsLetterSubscriberRepository;
import com.dxvalley.crowdfunding.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsLetterSubscriberServiceImpl implements NewsLetterSubscriberService {
    @Autowired
    NewsLetterSubscriberRepository newsLetterSubscriberRepository;
    @Autowired
    UserService userService;

    @Override
    public List<NewsLetterSubscriber> getAllSubscribers() {
        var subscribes = newsLetterSubscriberRepository.findAll();
        if(subscribes.size() == 0) throw new ResourceNotFoundException("Currently, There is no Subscribes");
        return subscribes;
    }

    @Override
    public NewsLetterSubscriber subscribe(Long userId, String email) {
        var user = userService.getUserById(userId);

        NewsLetterSubscriber newsLetterSubscriber = new NewsLetterSubscriber();
        System.out.println(email);
        newsLetterSubscriber.setEmail(email);
        newsLetterSubscriber.setUser(user);
        return newsLetterSubscriberRepository.save(newsLetterSubscriber);
    }
}
