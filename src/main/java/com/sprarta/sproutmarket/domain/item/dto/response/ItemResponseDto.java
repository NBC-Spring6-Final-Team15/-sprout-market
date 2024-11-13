package com.sprarta.sproutmarket.domain.item.dto.response;

import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String title;
    private String description;
    private int price;
    private String nickname;
    private ItemSaleStatus itemSaleStatus;
    private String categoryName;
    private Status status;
    private List<String> imageNames;

    @Builder
    public ItemResponseDto(Long id, String title, String description, int price, String nickname, ItemSaleStatus itemSaleStatus, String categoryName, Status status, List<ItemImage> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.nickname = nickname;
        this.itemSaleStatus = itemSaleStatus;
        this.categoryName = categoryName;
        this.status = status;
        this.imageNames = images.stream().map(ItemImage::getName).toList();
    }

    public static ItemResponseDto from(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice(),
                item.getSeller().getNickname(),
                item.getItemSaleStatus(),
                item.getCategory().getName(),
                item.getStatus(),
                item.getItemImages()
        );
    }
}
