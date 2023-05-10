package com.dxvalley.crowdfunding.messageManager.newsLetterSubscriber;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsLetter/")
@RequiredArgsConstructor
public class NewsLetterSubscriberController {
    private final NewsLetterSubscriberService newsLetterSubscriberService;

    @GetMapping("getAllSubscribers")
    ResponseEntity<?> getAllSubscribers() {
        var subscribers = newsLetterSubscriberService.getAllSubscribers();
        return new ResponseEntity<>(subscribers, HttpStatus.OK);
    }

    @GetMapping("getSubscriberByEmail/{email}")
    ResponseEntity<?> getSubscriberByEmail(@PathVariable String email) {
        var subscriber = newsLetterSubscriberService.getSubscriberByEmail(email);
        return new ResponseEntity<>(subscriber, HttpStatus.OK);
    }

    @PostMapping("subscribe/{email}")
    ResponseEntity<?> subscribe(@PathVariable String email) {
        var subscriber = newsLetterSubscriberService.subscribe(email);
        return new ResponseEntity<>(subscriber, HttpStatus.CREATED);
    }
}
