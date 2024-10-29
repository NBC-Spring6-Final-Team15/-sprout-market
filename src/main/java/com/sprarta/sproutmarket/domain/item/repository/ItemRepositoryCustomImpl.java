package com.sprarta.sproutmarket.domain.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemSearchResponse;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.entity.QItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemSearchResponse> searchItems(List<String> areaList, String searchKeyword, Category category, ItemSaleStatus saleStatus, Pageable pageable) {
        QItem qItem = QItem.item;

        BooleanBuilder builder = buildSearchConditions(areaList, searchKeyword, category, saleStatus, qItem);

        // 전체 개수 가져오기
        long total = queryFactory.selectFrom(qItem)
            .where(builder)
            .fetchCount();

        List<ItemSearchResponse> items = queryFactory.select(Projections.constructor(ItemSearchResponse.class,
                qItem.id,
                qItem.title,
                qItem.price,
                qItem.seller.address,
                qItem.images.get(0).name,
                qItem.createdAt))
            .from(qItem)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(items, pageable, total);
    }

    private BooleanBuilder buildSearchConditions(List<String> areaList, String searchKeyword, Category category, ItemSaleStatus saleStatus, QItem qItem) {
        BooleanBuilder builder = new BooleanBuilder();

        // 필수조건(근처 + 활성상태 매물)
        builder.and(qItem.seller.address.in(areaList))
            .and(qItem.status.eq(Status.ACTIVE));

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            String keyword = "%" + searchKeyword + "%";
            builder.and(qItem.title.likeIgnoreCase(keyword)
                .or(qItem.description.likeIgnoreCase(keyword)));
        }
        if (category != null) {
            builder.and(qItem.category.eq(category));
        }
        if (saleStatus != null) {
            builder.and(qItem.itemSaleStatus.eq(ItemSaleStatus.WAITING)); // 판매중인 매물만 뜨게
        }

        return builder;
    }
}
