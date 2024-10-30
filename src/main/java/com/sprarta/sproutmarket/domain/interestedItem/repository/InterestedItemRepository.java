package com.sprarta.sproutmarket.domain.interestedItem.repository;

import com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestedItemRepository extends JpaRepository<InterestedItem, Long> {

    // 특정 아이템을 관심 상품으로 설정한 사용자 목록 조회
    @Query("SELECT ii FROM InterestedItem ii WHERE ii.item.id = :itemId")
    List<InterestedItem> findByItemId(Long itemId);

    // 특정 사용자의 관심 상품 목록을 페이징하여 조회
    Page<InterestedItem> findByUser(User user, Pageable pageable);

    @Query("SELECT ii FROM InterestedItem ii WHERE ii.user = :user AND ii.item = :item")
    Optional<InterestedItem> findByUserAndItem(User user, Item item);
}
