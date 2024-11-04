package com.sprarta.sproutmarket.domain.image.itemImage.entity;

import com.sprarta.sproutmarket.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ItemImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String name;

    @Builder
    public ItemImage(String name, Item item){
        this.name = name;
        this.item = item;
    }
}
