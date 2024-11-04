package com.sprarta.sproutmarket.domain.interestedItem.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem;
import com.sprarta.sproutmarket.domain.interestedItem.repository.InterestedItemRepository;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InterestedItemService {

    private final InterestedItemRepository interestedItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void addInterestedItem(Long itemId, CustomUserDetails authUser) {
        // 현재 인증된 사용자를 데이터베이스에서 가져옴
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        // 해당 ID의 상품을 데이터베이스에서 가져옴
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_ITEM));

        // 관심 상품이 이미 존재하는지 확인
        if (interestedItemRepository.findByUserAndItem(user, item).isPresent()) {
            throw new ApiException(ErrorStatus.ALREADY_INTERESTED_ITEM);
        }

        // 생성자를 통해 InterestedItem 객체 생성
        InterestedItem interestedItem = new InterestedItem(user, item);

        // 관심 상품을 저장
        interestedItemRepository.save(interestedItem);
    }

    @Transactional
    public void removeInterestedItem(Long itemId, CustomUserDetails authUser) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_ITEM));

        // 관심 상품 존재 여부 확인 및 삭제
        InterestedItem interestedItem = interestedItemRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_INTERESTED_ITEM));

        interestedItemRepository.delete(interestedItem);
    }

    /**
     * 특정 유저의 관심 상품을 페이지네이션 방식으로 조회하는 메서드
     * @param authUser 현재 인증된 사용자 정보
     * @param page 페이지 번호(1부터 시작)
     * @param size 페이지당 조회할 관심 상품 개수
     * @return Page<ItemResponseDto> 페이지네이션된 관심 상품 목록을 반환
     */
    public Page<ItemResponseDto> getInterestedItems(CustomUserDetails authUser, int page, int size) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<InterestedItem> interestedItemsPage = interestedItemRepository.findByUser(user, pageable);

        // 관심 상품을 ItemResponse 로 변환하여 반환
        return interestedItemsPage.map(interestedItem -> {
            Item item = interestedItem.getItem();
            return new ItemResponseDto(
                    item.getId(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getPrice(),
                    item.getSeller().getNickname(),
                    item.getItemSaleStatus(),
                    item.getCategory().getName(),
                    item.getStatus()
            );
        });
    }

    /*
     * 특정 아이템에 관심 있는 사용자 목록을 반환하는 메서드
     * @param itemId 관심 상품의 ID
     * @return List<User> 관심 상품에 관심을 가진 사용자 목록
     */
    public List<User> findUsersByInterestedItem(Long itemId) {
        // 해당 아이템에 관심이 있는 InterestedItem 리스트를 조회하고
        List<InterestedItem> interestedItems = interestedItemRepository.findByItemId(itemId);

        List<User> users = interestedItems.stream()
                .map(InterestedItem::getUser)
                .collect(Collectors.toList());

        // 로그 추가: 관심 상품에 등록된 사용자가 있는지 확인
        log.info("Found {} interested users for item {}", users.size(), itemId);

        return users;
    }
}
