package com.sprarta.sproutmarket.domain.item.repository;

import com.sprarta.sproutmarket.domain.areas.dto.AdmNameDto;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
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

    Optional<Item> findByIdAndSeller(@Param("id") Long id, @Param("seller") User seller);

    default Item findByIdAndSellerIdOrElseThrow(Long itemId, User seller) {
        return findByIdAndSeller(itemId, seller)
            .orElseThrow(() -> new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_ITEM));
    }

    @Query("SELECT i FROM Item i " +
        "JOIN FETCH i.category " +
        "JOIN FETCH i.seller " +
        "WHERE i.seller = :seller")
    Page<Item> findBySeller(Pageable pageable, @Param("seller") User seller);

    @Query("SELECT i FROM Item i " +
        "JOIN FETCH i.seller " +
        "JOIN FETCH i.category " +
        "WHERE i.category = :category")
    Page<Item> findByCategory(Pageable pageable, @Param("category") Category findCategory);

    @Query("SELECT i FROM Item i JOIN FETCH i.seller WHERE i.seller.address IN :areaList")
    Page<Item> findByAreaListAndUserArea(Pageable pageable, @Param("areaList") List<String> areaList);

    @Query("SELECT i FROM Item i JOIN FETCH i.seller WHERE i.seller.address IN :areaList AND i.category.id = :categoryId")
    Page<Item> findItemByAreaAndCategory(Pageable pageable, @Param("areaList") List<String> areaList, @Param("categoryId") Long categoryId);

}
