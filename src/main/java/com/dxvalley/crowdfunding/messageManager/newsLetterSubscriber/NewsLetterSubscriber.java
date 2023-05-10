package com.dxvalley.crowdfunding.messageManager.newsLetterSubscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class NewsLetterSubscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long newsLetterSubscriberId;
    private String email;
    private String subscribedAt;
}
