package com.sprarta.sproutmarket.domain.item.repository;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemSearchResponse;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    Page<ItemSearchResponse> searchItems(List<String> areaList, String searchKeyword, Category category, ItemSaleStatus saleStatus, Pageable pageable);
}
