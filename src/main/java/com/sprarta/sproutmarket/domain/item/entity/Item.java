package com.sprarta.sproutmarket.domain.item.entity;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.image.entity.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private int price;

    // 판매 상태
    @Column(nullable = false)
    private ItemSaleStatus itemSaleStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // 파일
    @Column(nullable = false)
    @OneToMany(mappedBy = "item_id")
    private List<Image> images = new ArrayList<>();

    // 삭제 상태
    @Column(nullable = false)
    private Status status;





}
