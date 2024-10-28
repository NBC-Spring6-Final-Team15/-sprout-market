package com.sprarta.sproutmarket.domain.image.entity;

import com.sprarta.sproutmarket.domain.item.entity.Item;
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
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String name;

    @Builder
    public Image(String name, Item item){
        this.name = name;
        this.item = item;
    }
}
