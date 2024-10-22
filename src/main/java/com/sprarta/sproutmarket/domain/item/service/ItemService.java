package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.service.CategoryService;
import com.sprarta.sproutmarket.domain.common.dto.response.StatusResponse;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.eto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.eto.response.ItemSimpleResponse;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;

    /**
     * 로그인한 사용자가 중고 물품을 등록하는 로직
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id)
     * @return StatusResponse - 사용자 이메일
     */
    public StatusResponse createItem(ItemCreateRequest itemCreateRequest){
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기

        // 유저 활성 상태 확인

        // 카테고리 찾기
        Category findCategory = categoryService.findByIdOrElseThrow(itemCreateRequest.getCategoryId());


        Item item = Item.builder()
            .title(itemCreateRequest.getTitle())
            .description(itemCreateRequest.getDescription())
            .price(itemCreateRequest.getPrice())
            .itemSaleStatus(ItemSaleStatus.WAITING)
            .category(findCategory)
            .status(Status.ACTIVE)
            .build();

        itemRepository.save(item);

        return new StatusResponse(
            "매물이 성공적으로 등록되었습니다.",
            "",
            200
        );
    }

    // 물품 상태 변경

    // 물품 내용 수정

    // 물품 물리적 삭제



}
