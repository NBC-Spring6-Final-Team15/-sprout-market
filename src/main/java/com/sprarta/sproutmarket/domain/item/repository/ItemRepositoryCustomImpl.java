package com.sprarta.sproutmarket.domain.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
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
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<Item> searchItems(List<String> areaList, String searchKeyword, Category category, ItemSaleStatus saleStatus, Pageable pageable) {
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();

        // 필수조건(근처 + 활성상태 매물)
        builder.and(item.seller.address.in(areaList))
                .and(item.status.eq(Status.ACTIVE));

        if(searchKeyword != null && !searchKeyword.isEmpty()){
            String keyword = "%" + searchKeyword + "%";
            builder.and(item.title.likeIgnoreCase(keyword)
                .or(item.description.likeIgnoreCase(keyword)));
        }
        if(category != null) {
            builder.and(item.category.eq(category));
        }
        if(saleStatus != null) {
            builder.and(item.itemSaleStatus.eq(ItemSaleStatus.WAITING));    // 판매중인 매물만 뜨게
        }

        // 전체 개수 가져오기
        long total = queryFactory.selectFrom(item)
                                .where(builder)
                                .fetchCount();

        // 결과 가져오기(pageable)
        List<Item> items = queryFactory.selectFrom(item)
                                        .where(builder)
                                        .offset(pageable.getOffset())
                                        .limit(pageable.getPageSize())
                                        .fetch();

        return new PageImpl<>(items, pageable, total);
    }
}
