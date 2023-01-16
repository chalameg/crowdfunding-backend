package com.dxvalley.crowdfunding.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
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
    private Boolean emailVerified;
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
    @Transient
    private Campaign campaigns;

    public Users( String username, String password, String fullName, String website,
        String biography, Boolean emailVerified, String createdAt, String deletedAt, Boolean isEnabled) {
        this.password = new BCryptPasswordEncoder().encode(password);
        this.fullName = fullName;
        this.username= username;
        this.website= website;
        this.biography= biography;
        this.emailVerified= emailVerified;
        this.createdAt=createdAt;
        this.deletedAt=deletedAt;
        this.isEnabled=isEnabled;
    }


    public Users orElseThrow(Object object) {
        return null;
    }
    
}
