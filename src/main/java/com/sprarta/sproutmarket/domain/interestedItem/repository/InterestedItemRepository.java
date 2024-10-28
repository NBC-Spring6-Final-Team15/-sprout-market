package com.sprarta.sproutmarket.domain.interestedItem.repository;

import com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem;
import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestedItemRepository extends JpaRepository<InterestedItem, Long> {
    List<InterestedItem> findByItemId(Long itemId);
    Page<InterestedItem> findByUser(User user, Pageable pageable);
}
