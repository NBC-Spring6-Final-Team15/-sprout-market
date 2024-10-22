package com.sprarta.sproutmarket.domain.user.entity;

import com.sprarta.sproutmarket.domain.common.Timestamped;
import com.sprarta.sproutmarket.domain.report.entity.Report;
import com.sprarta.sproutmarket.domain.review.entity.Review;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

//    @ManyToOne
//    @JoinColumn(name = "activity_address_id")
//    private ActivityArea activityAddress;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Report> reports;

    public User(String username, String email, String password, String nickname, String phoneNumber, String address, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userRole = userRole;
    }
}