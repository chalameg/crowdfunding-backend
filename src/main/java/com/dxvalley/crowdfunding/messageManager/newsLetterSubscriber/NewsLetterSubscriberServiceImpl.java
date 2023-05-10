package com.dxvalley.crowdfunding.messageManager.newsLetterSubscriber;

import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsLetterSubscriberServiceImpl implements NewsLetterSubscriberService {

    private final NewsLetterSubscriberRepository newsLetterSubscriberRepository;
    private final DateTimeFormatter dateTimeFormatter;
    @Override
    public List<NewsLetterSubscriber> getAllSubscribers() {
        var subscribes = newsLetterSubscriberRepository.findAll();
        if (subscribes.isEmpty()) throw new ResourceNotFoundException("Currently, There is no Subscribes");
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
