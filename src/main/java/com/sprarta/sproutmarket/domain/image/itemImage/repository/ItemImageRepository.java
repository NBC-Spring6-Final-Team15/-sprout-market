package com.sprarta.sproutmarket.domain.image.itemImage.repository;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

    @Query("SELECT i FROM ItemImage i WHERE i.name = :imageAddress")
    Optional<ItemImage> findByName(@Param("imageAddress") String imageAddress);

    default ItemImage findByNameOrElseThrow(String imageAddress) {
        return findByName(imageAddress)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_IMAGE));
    }

    default ItemImage findByIdOrElseThrow(Long imageId) {
        return findById(imageId)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_IMAGE));
    }

    List<ItemImage> findByUserIdAndItemIsNull(Long id);

    @Modifying
    @Query(value = "SELECT * FROM item_image ii where ii.item_id Is Null AND ii.created_at <= DATE_SUB(NOW(), INTERVAL 1 HOUR)", nativeQuery = true)
    List<ItemImage> findByItemIsNullAndExpired();
}
