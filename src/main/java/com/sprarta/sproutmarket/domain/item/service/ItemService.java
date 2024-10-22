package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.service.CategoryService;
import com.sprarta.sproutmarket.domain.common.dto.response.StatusResponse;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.exception.NotFoundException;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.exception.ItemNotFoundException;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
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
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id)
     * @param authUser 매물 수정을 유청한 사용자
     * @return StatusResponse - 생성된 아이템에 대한 메세지, 사용자 이메일, 상태 코드를 포함한 응답 객체
     */
    @Transactional
    public StatusResponse createItem(ItemCreateRequest itemCreateRequest, CustomUserDetails authUser){
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
            .orElseThrow(() -> new NotFoundException("User not found"));

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

        return new StatusResponse(
            "매물이 성공적으로 등록되었습니다.",
            user.getEmail(),
            200
        );
    }

    /**
     * 매물의 판매 상태만을 변경하는 로직
     * @param itemId Item's ID
     * @param itemSaleStatus 판매 상태
     * @param authUser 매물 판매 상태 수정을 유청한 사용자
     * @return StatusResponse - 생성된 아이템에 대한 메세지, 사용자 이메일, 상태 코드를 포함한 응답 객체
     */
    @Transactional
    public StatusResponse updateSaleStatus(Long itemId, String itemSaleStatus, CustomUserDetails authUser) {
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = findByIdAndSellerIdOrElseThrow(itemId, user.getId());

        item.changeSaleStatus(ItemSaleStatus.of(itemSaleStatus));

        itemRepository.save(item);

        return new StatusResponse(
            "매물의 판매 상태가 성공적으로 수정되었습니다.",
            user.getEmail(),
            200
        );
    }

    /**
     *
     * @param itemId Item's ID
     * @param itemContentsUpdateRequest 매물 수정 정보를 포함한 요청 객체(제목, 내용, 가격, 이미지URL)
     * @param authUser 매물 내용 수정을 유청한 사용자
     * @return StatusResponse - 생성된 아이템에 대한 메세지, 사용자 이메일, 상태 코드를 포함한 응답 객체
     */
    @Transactional
    public StatusResponse updateContents(Long itemId, ItemContentsUpdateRequest itemContentsUpdateRequest, CustomUserDetails authUser){
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = findByIdAndSellerIdOrElseThrow(itemId, user.getId());

        item.changeContents(
            itemContentsUpdateRequest.getTitle(),
            itemContentsUpdateRequest.getDescription(),
            itemContentsUpdateRequest.getPrice(),
            itemContentsUpdateRequest.getImageUrl()
        );

        itemRepository.save(item);

        return new StatusResponse(
            "매물의 내용이 성공적으로 수정되었습니다.",
            user.getEmail(),
            200
        );
    }


    /**
     *
     * @param itemId Item's ID
     * @param authUser 매물 내용 수정을 유청한 사용자
     * @return StatusResponse - 생성된 아이템에 대한 메세지, 사용자 이메일, 상태 코드를 포함한 응답 객체
     */
    @Transactional
    public StatusResponse solfDeleteItem(Long itemId, CustomUserDetails authUser){
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = userRepository.findById(authUser.getId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = findByIdAndSellerIdOrElseThrow(itemId, user.getId());

        item.solfDelete(
            Status.DELETED
        );

        itemRepository.save(item);

        return new StatusResponse(
            "매물이 성공적으로 삭제되었습니다.",
            user.getEmail(),
            200
        );
    }


    /**
     * 주어진 id에 해당하는 Item을 찾고,
     * 존재하지 않을 경우 ItemNotFoundException을 던집니다.
     * @param id Item's ID
     * @return Item 객체
     * @throws ItemNotFoundException 해당 id의 매물이 존재하지 않을 경우 발생
     */
    public Item findByIdOrElseThrow(Long id){
        return itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException());
    }

    /**
     * 주어진 id에 해당하는 Item을 찾고,
     * 존재하지 않을 경우 ItemNotFoundException을 던집니다.
     * @param itemId Item's ID
     * @param sellerId User's ID
     * @return Item 객체
     * @throws NotFoundException 해당 id의 사용자 id와 입력받은 sellerId와 동일하지 않을 경우 발생
     */
    public Item findByIdAndSellerIdOrElseThrow(Long itemId, Long sellerId){
        return itemRepository.findByIdAndSellerId(itemId, sellerId)
            .orElseThrow(() -> new NotFoundException("해당 매물을 올린 사용자와 일치하지않습니다."));
    }

}
