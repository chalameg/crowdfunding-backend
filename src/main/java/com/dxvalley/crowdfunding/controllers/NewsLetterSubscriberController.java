package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.notification.NewsLetterSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsLetter/")
public class NewsLetterSubscriberController {
    @Autowired
    NewsLetterSubscriberService newsLetterSubscriberService;

    @GetMapping("getAllSubscribers")
    ResponseEntity<?> getAllSubscribers(){
        var subscribers = newsLetterSubscriberService.getAllSubscribers();
        return new ResponseEntity<>(subscribers, HttpStatus.OK);
    }
    @PostMapping("subscribe/{userId}/{email}")
    ResponseEntity<?> subscribe(@PathVariable Long userId, @PathVariable String email){
        var subscriber = newsLetterSubscriberService.subscribe(userId,email);
        return new ResponseEntity<>(subscriber, HttpStatus.CREATED);
    }
}
