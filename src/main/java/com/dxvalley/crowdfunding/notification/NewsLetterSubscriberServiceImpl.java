package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.exceptions.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.NewsLetterSubscriber;
import com.dxvalley.crowdfunding.repositories.NewsLetterSubscriberRepository;
import com.dxvalley.crowdfunding.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NewsLetterSubscriberServiceImpl implements NewsLetterSubscriberService {
    @Autowired
    NewsLetterSubscriberRepository newsLetterSubscriberRepository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<NewsLetterSubscriber> getAllSubscribers() {
        var subscribes = newsLetterSubscriberRepository.findAll();
        if (subscribes.size() == 0) throw new ResourceNotFoundException("Currently, There is no Subscribes");
        return subscribes;
    }

    @Override
    public NewsLetterSubscriber subscribe(String email) {
        var subscriber = newsLetterSubscriberRepository.findByEmail(email);
        if (subscriber.isPresent())
            throw new ResourceAlreadyExistsException("There is already subscription with this email");
        NewsLetterSubscriber newsLetterSubscriber = new NewsLetterSubscriber();
        newsLetterSubscriber.setEmail(email);
        newsLetterSubscriber.setSubscribedAt(LocalDateTime.now().format(dateTimeFormatter));
        return newsLetterSubscriberRepository.save(newsLetterSubscriber);
    }

    @Override
    public NewsLetterSubscriber getSubscriberByEmail(String email) {
        var subscriber = newsLetterSubscriberRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("There is no Subscriber with this email")
        );
        return subscriber;
    }
}
