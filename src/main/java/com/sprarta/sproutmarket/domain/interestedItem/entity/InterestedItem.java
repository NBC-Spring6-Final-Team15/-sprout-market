package com.sprarta.sproutmarket.domain.interestedItem.entity;

import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interested_item")
public class InterestedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 관심 상품과 사용자 간의 양방향 관계를 위한 메서드
    public void setUser(User user) {
        this.user = user;
        // 관심 상품이 추가될 때 사용자의 관심 상품 목록에도 추가
        user.getInterestedItems().add(this);
    }
}
