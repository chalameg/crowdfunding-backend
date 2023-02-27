package com.dxvalley.crowdfunding.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class NewsLetterSubscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long newsLetterSubscriberId;
    private String email;
    private String subscribedAt;
}
