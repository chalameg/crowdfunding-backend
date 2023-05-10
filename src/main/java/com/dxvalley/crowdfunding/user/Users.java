package com.dxvalley.crowdfunding.user;

import com.dxvalley.crowdfunding.user.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "Users_username_unique",
                columnNames = "username"
        )
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String biography;
    private String website;
    private String password;
    private String createdAt;
    private String editedAt;
    private String address;
    private Boolean isEnabled;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    private Short totalCampaigns;
    private Short contributions;
    private Double totalAmountSpent;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

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
        this.userStatus = UserStatus.ACTIVE;
    }

}