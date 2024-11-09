package com.sprarta.sproutmarket.domain.item.repository;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    default Item findByIdOrElseThrow(Long id) {
        return findById(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_ITEM));
    }

    @Query("SELECT i FROM Item i WHERE i.id = :id AND i.seller.id = :userId")
    Optional<Item> findByIdAndSellerId(@Param("id") Long id, @Param("userId") Long userId);

    default Item findByIdAndSellerIdOrElseThrow(Long itemId, Long sellerId) {
        return findByIdAndSellerId(itemId, sellerId)
            .orElseThrow(() -> new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_ITEM));
    }

    @Query("SELECT i FROM Item i " +
        "JOIN FETCH i.category " +
        "JOIN FETCH i.seller " +
        "WHERE i.seller = :seller AND i.status = 'ACTIVE' ")
    Page<Item> findBySeller(Pageable pageable, @Param("seller") User seller);

    @Query("SELECT i FROM Item i JOIN FETCH i.seller WHERE i.seller.address IN :areaList AND i.status = 'ACTIVE' ORDER BY i.timeForOrder DESC")
    Page<Item> findByAreaListAndUserArea(Pageable pageable, @Param("areaList") List<String> areaList);

    @Query("SELECT i FROM Item i JOIN FETCH i.seller WHERE i.seller.address IN :areaList AND i.status = 'ACTIVE'")
    List<Item> findByUserArea(@Param("areaList") List<String> areaList);

    @Query("SELECT i FROM Item i JOIN FETCH i.seller WHERE i.seller.address IN :areaList AND i.category.id = :categoryId AND i.status = 'ACTIVE' ORDER BY i.timeForOrder DESC")
    Page<Item> findItemByAreaAndCategory(Pageable pageable, @Param("areaList") List<String> areaList, @Param("categoryId") Long categoryId);

    boolean existsByIdAndSellerId(Long itemId, Long userId);
}
