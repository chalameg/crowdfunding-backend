package com.dxvalley.crowdfunding.messageManager.newsLetterSubscriber;

import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsLetterSubscriberServiceImpl implements NewsLetterSubscriberService {

    private final NewsLetterSubscriberRepository newsLetterSubscriberRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public List<NewsLetterSubscriber> getAllSubscribers() {
        List<NewsLetterSubscriber> subscribes = this.newsLetterSubscriberRepository.findAll();
        if (subscribes.isEmpty()) {
            throw new ResourceNotFoundException("Currently, There is no Subscribes");
        } else {
            return subscribes;
        }
    }

    public NewsLetterSubscriber subscribe(String email) {
        Optional<NewsLetterSubscriber> subscriber = this.newsLetterSubscriberRepository.findByEmail(email);
        if (subscriber.isPresent()) {
            throw new ResourceAlreadyExistsException("There is already subscription with this email");
        } else {
            NewsLetterSubscriber newsLetterSubscriber = new NewsLetterSubscriber();
            newsLetterSubscriber.setEmail(email);
            newsLetterSubscriber.setSubscribedAt(LocalDateTime.now().format(this.dateTimeFormatter));
            return (NewsLetterSubscriber) this.newsLetterSubscriberRepository.save(newsLetterSubscriber);
        }
    }

    public NewsLetterSubscriber getSubscriberByEmail(String email) {
        NewsLetterSubscriber subscriber = (NewsLetterSubscriber) this.newsLetterSubscriberRepository.findByEmail(email).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no Subscriber with this email");
        });
        return subscriber;
    }
}
