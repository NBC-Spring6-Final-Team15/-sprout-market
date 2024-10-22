package com.sprarta.sproutmarket.domain.item.repository;

import com.sprarta.sproutmarket.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
