package com.sprarta.sproutmarket.domain.user.entity;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.Timestamped;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory;
import com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.report.entity.Report;
import com.sprarta.sproutmarket.domain.review.entity.Review;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    @Column(name = "rate")
    private int rate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Report> reports;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<InterestedItem> interestedItems = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<InterestedCategory> interestedCategories = new ArrayList<>();

    @Column(nullable = true)
    private String profileImageUrl;

    public User(String username, String email, String password, String nickname, String phoneNumber, String address, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userRole = userRole;
    }

    public User(Long id, String username, String email, String password, String nickname, String phoneNumber, String address, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userRole = userRole;
    }

    public User(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    public static User fromAuthUser(CustomUserDetails customUserDetails) {
        return new User(customUserDetails.getId(), customUserDetails.getEmail(), customUserDetails.getRole());
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void deactivate() {
        this.status = Status.DELETED;
    }

    public void activate() { this.status = Status.ACTIVE; }

    public void plusRate() {
        this.rate++;
    }

    public void minusRate() {
        this.rate--;
    }

    public void changeAddress(String newAddress) {
        if (newAddress == null || newAddress.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 주소입니다.");
        }
        this.address = newAddress;
    }

    // 양방향 관계 설정을 위한 메서드
    public void addInterestedItem(InterestedItem interestedItem) {
        interestedItems.add(interestedItem);
        interestedItem.setUser(this);
    }

    // 관심 상품 제거 메서드
    public void removeInterestedItem(Item item) {
        interestedItems.removeIf(interestedItem -> interestedItem.getItem().equals(item));
    }

    // 관심 카테고리 추가 메서드
    public void addInterestedCategory(InterestedCategory interestedCategory) {
        interestedCategories.add(interestedCategory);
        interestedCategory.setUser(this);
    }

    // 관심 카테고리 제거 메서드
    public void removeInterestedCategory(Category category) {
        interestedCategories.removeIf(interestedCategory -> interestedCategory.getCategory().equals(category));
    }

    // 프로필 이미지 업데이트 메서드
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}