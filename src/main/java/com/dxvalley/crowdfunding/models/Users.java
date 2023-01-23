package com.dxvalley.crowdfunding.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "Users_email_unique",
                columnNames = "username"
        )
})
@Getter
@Setter
@RequiredArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userId;
    private String username;
    private String fullName;
    private String biography;
    private String website;
    private String password;
    private String createdAt;
    private String deletedAt;
    private Boolean isEnabled;

    //user roles
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    //user address
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    //user campaign
    @OneToMany(targetEntity = Campaign.class, cascade = CascadeType.ALL)
    private List<Campaign> campaigns;

    public Users( String username, String password, String fullName, String website,
                  String biography, String createdAt, String deletedAt, Boolean isEnabled) {
        this.password = new BCryptPasswordEncoder().encode(password);
        this.fullName = fullName;
        this.username= username;
        this.website= website;
        this.biography= biography;
        this.createdAt=createdAt;
        this.deletedAt=deletedAt;
        this.isEnabled=isEnabled;
    }

    public Users orElseThrow(Object object) {
        return null;
    }

}