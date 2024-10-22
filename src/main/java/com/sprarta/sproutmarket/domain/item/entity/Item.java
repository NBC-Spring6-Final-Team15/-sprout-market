package com.sprarta.sproutmarket.domain.item.entity;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.Timestamped;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.image.entity.Image;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "items")
public class Item extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private int price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sellerId;

    // 판매 상태
    @Column(nullable = false)
    private ItemSaleStatus itemSaleStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // 파일
//    @Column(nullable = false)
//    @OneToMany(mappedBy = "item_id")
//    private List<Image> images = new ArrayList<>();


    // 삭제 상태
    @Column(nullable = false)
    private Status status;


    // 빌더의 사용이유: 필드 개수가 많고, 더 추가될 예정이라
    @Builder
    private Item(String title, String description, int price, ItemSaleStatus itemSaleStatus, Category category, Status status){
        this.title = title;
        this.description = description;
        this.price = price;
        this.itemSaleStatus = itemSaleStatus;
        this.category = category;
        this.status = status;
    }

    public void changeSaleStatus(ItemSaleStatus itemSaleStatus) {
        this.itemSaleStatus = itemSaleStatus;
    }

    public void changeContents(String title, String description, int price, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public void solfDelete(Status deleted) {
        this.status = deleted;
    }
}
