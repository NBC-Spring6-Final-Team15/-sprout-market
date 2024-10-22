package com.sprarta.sproutmarket.domain.item.repository;

import com.sprarta.sproutmarket.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.id = :itemId AND i.sellerId.id = :sellerId")
    Optional<Item> findByIdAndSellerId(@Param("itemId") Long itemId, @Param("sellerId") Long sellerId);
}
