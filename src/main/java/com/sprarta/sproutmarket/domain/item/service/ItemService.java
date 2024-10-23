package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.service.CategoryService;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.report.entity.Report;
import com.sprarta.sproutmarket.domain.report.repository.ReportRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;


    /**
     * 로그인한 사용자가 중고 물품을 등록하는 로직
     *
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id)
     * @param authUser          매물 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 가격, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse createItem(ItemCreateRequest itemCreateRequest, CustomUserDetails authUser) {
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        // 카테고리 찾기
        Category findCategory = categoryService.findByIdOrElseThrow(itemCreateRequest.getCategoryId());


        Item item = Item.builder()
                .title(itemCreateRequest.getTitle())
                .description(itemCreateRequest.getDescription())
                .price(itemCreateRequest.getPrice())
                .itemSaleStatus(ItemSaleStatus.WAITING)
                .category(findCategory)
                .seller(user)
                .status(Status.ACTIVE)
                .build();

        itemRepository.save(item);

        return new ItemResponse(
                item.getTitle(),
                item.getPrice(),
                user.getNickname()
        );
    }

    /**
     * 매물의 판매 상태만을 변경하는 로직
     *
     * @param itemId         Item's ID
     * @param itemSaleStatus 판매 상태
     * @param authUser       매물 판매 상태 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 가격, 판매상태, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse updateSaleStatus(Long itemId, String itemSaleStatus, CustomUserDetails authUser) {
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = findByIdAndSellerIdOrElseThrow(itemId, user.getId());

        item.changeSaleStatus(ItemSaleStatus.of(itemSaleStatus));

        itemRepository.save(item);

        return new ItemResponse(
                item.getTitle(),
                item.getPrice(),
                item.getItemSaleStatus(),
                user.getNickname()
        );
    }

    /**
     * 매물의 내용(제목, 설명, 가격, 이미지URL)을 수정하는 로직
     *
     * @param itemId                    Item's ID
     * @param itemContentsUpdateRequest 매물 수정 정보를 포함한 요청 객체(제목, 내용, 가격, 이미지URL)
     * @param authUser                  매물 내용 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 설명, 가격, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse updateContents(Long itemId, ItemContentsUpdateRequest itemContentsUpdateRequest, CustomUserDetails authUser) {
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = findByIdAndSellerIdOrElseThrow(itemId, user.getId());

        item.changeContents(
                itemContentsUpdateRequest.getTitle(),
                itemContentsUpdateRequest.getDescription(),
                itemContentsUpdateRequest.getPrice(),
                itemContentsUpdateRequest.getImageUrl()
        );

        itemRepository.save(item);

        return new ItemResponse(
                item.getTitle(),
                item.getDescription(),
                item.getPrice(),
                user.getNickname()
        );
    }


    /**
     * 매물을 삭제하는 로직
     *
     * @param itemId   Item's ID
     * @param authUser 매물 삭제를 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 가격, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse softDeleteItem(Long itemId, CustomUserDetails authUser) {
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = findByIdAndSellerIdOrElseThrow(itemId, user.getId());

        item.solfDelete(
                Status.DELETED
        );

        itemRepository.save(item);

        return new ItemResponse(
                item.getTitle(),
                item.getStatus(),
                user.getNickname()
        );
    }

    /**
     * 주어진 id에 해당하는 Item을 찾고,
     * 존재하지 않을 경우 ItemNotFoundException을 던집니다.
     *
     * @param id Item's ID
     * @return Item 객체
     * @throws ApiException 해당 id의 매물이 존재하지 않을 경우 발생
     */
    public Item findByIdOrElseThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_ITEM));
    }

    /**
     * 주어진 id에 해당하는 Item을 찾고,
     * 존재하지 않을 경우 ItemNotFoundException을 던집니다.
     *
     * @param itemId   Item's ID
     * @param sellerId User's ID
     * @return Item 객체
     * @throws ApiException 해당 id의 사용자 id와 입력받은 sellerId와 동일하지 않을 경우 발생
     */
    public Item findByIdAndSellerIdOrElseThrow(Long itemId, Long sellerId) {
        return itemRepository.findByIdAndSellerId(itemId, sellerId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_OWNED_ITEM));
    }

}
