package com.sprarta.sproutmarket.domain.item.entity;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.Timestamped;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User seller;
    // 판매 상태
    @Enumerated(EnumType.STRING)
    private ItemSaleStatus itemSaleStatus = ItemSaleStatus.WAITING;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    // 파일
    @Column(nullable = false)
    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE)
    private List<ItemImage> itemImages;
    // 삭제 상태
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    // 조회 순서를 위한 시간
    private LocalDateTime timeForOrder;

    public Item(String title, String description, int price, User seller, Category category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.seller = seller;
        this.category = category;
        this.timeForOrder = LocalDateTime.now();
    }

    public void changeSaleStatus(ItemSaleStatus itemSaleStatus) {
        this.itemSaleStatus = itemSaleStatus;
    }

    public void changeContents(String title, String description, int price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public void solfDelete(Status deleted) {
        this.status = deleted;
    }

    public void boostItem() {
        this.timeForOrder = LocalDateTime.now();
    }

    public void fetchImage(List<ItemImage> images) {
        this.itemImages = images;
    }

}
