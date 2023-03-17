package com.dxvalley.crowdfunding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long collaboratorId;
    private Boolean isAccepted;
    @OneToOne
    @JoinColumn(name = "userId")
    Users users;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    private String invitationSentAt;
    private String expiredAt;
    private String respondedAt;

    public Collaborator(Long collaboratorId, Users users, String invitationSentAt, String respondedAt) {
        this.collaboratorId = collaboratorId;
        this.users = users;
        this.invitationSentAt = invitationSentAt;
        this.respondedAt = respondedAt;
    }
}
