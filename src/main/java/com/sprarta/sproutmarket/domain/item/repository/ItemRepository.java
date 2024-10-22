package com.sprarta.sproutmarket.domain.item.repository;

import com.sprarta.sproutmarket.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndSellerId(Long itemId, Long sellerId);
}
