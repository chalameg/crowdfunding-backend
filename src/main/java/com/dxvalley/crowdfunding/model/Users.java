package com.dxvalley.crowdfunding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "Users_username_unique",
                columnNames = "username"
        )
})
@Getter
@Setter
@RequiredArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String username;
    private String fullName;
    private String biography;
    private String website;
    private String password;
    private String createdAt;
    private String editedAt;
    private Boolean isEnabled;
    private String avatarUrl;
    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    //user campaign
    @OneToMany(targetEntity = Campaign.class, cascade = CascadeType.ALL)
    private List<Campaign> campaigns;

    public Users(String username, String password, String fullName, String website,
                 String biography, String createdAt, String avatarUrl,
                 Boolean isEnabled, String address) {
        this.password = new BCryptPasswordEncoder().encode(password);
        this.fullName = fullName;
        this.username = username;
        this.website = website;
        this.biography = biography;
        this.createdAt = createdAt;
        this.isEnabled = isEnabled;
        this.avatarUrl = avatarUrl;
        this.address = address;
    }

}