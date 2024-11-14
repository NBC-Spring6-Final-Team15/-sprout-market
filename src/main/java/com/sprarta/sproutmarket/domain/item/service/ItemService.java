package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
import com.sprarta.sproutmarket.domain.interestedCategory.service.InterestedCategoryService;
import com.sprarta.sproutmarket.domain.interestedItem.service.InterestedItemService;
import com.sprarta.sproutmarket.domain.item.dto.request.FindItemsInMyAreaRequestDto;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemSearchRequest;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemSearchResponse;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.entity.ItemWithViewCount;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepositoryCustom;
import com.sprarta.sproutmarket.domain.notification.entity.PriceChangeEvent;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AdministrativeAreaService admAreaService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final InterestedItemService interestedItemService;
    private final RedisTemplate<String, Long> redisTemplate;
    private final InterestedCategoryService interestedCategoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemImageRepository itemImageRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * 중고 매물에 대해서 검색하는 로직
     *
     * @param page              페이지당 카드 수
     * @param size              현재 인증된 사용자 정보
     * @param itemSearchRequest 매물 검색 조건을 포함한 요청 객체(키워드, 카테고리id, 판매상태)
     * @param authUser          매물 수정을 요청한 사용자
     * @return Page<ItemResponseDto> - 요청된 페이지에 해당하는 검색 조건에 맞는 매물 목록을 포함한 페이지 정보
     * 각 매물은 ItemResponseDto 형태로 변환되어 반환됨
     */
    public Page<ItemSearchResponse> searchItems(int page, int size, ItemSearchRequest itemSearchRequest, CustomUserDetails authUser) {
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        List<String> areaList = admAreaService.getAdmNameListByAdmName(user.getAddress());
        Category category = categoryRepository.findByIdAndStatusIsActiveOrElseThrow(itemSearchRequest.getCategoryId());
        ItemSaleStatus itemSaleStatus = setSaleStatus(itemSearchRequest);
        Pageable pageable = PageRequest.of(page - 1, size);

        return itemRepositoryCustom.searchItems(areaList, itemSearchRequest.getSearchKeyword(), category, itemSaleStatus, pageable);
    }

    /**
     * 로그인한 사용자가 중고 물품을 등록하는 로직
     *
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id, 업로드한 이미지 이름)
     * @param authUser          매물 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 가격, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse addItem(ItemCreateRequest itemCreateRequest, CustomUserDetails authUser) {
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        Category category = categoryRepository.findByIdAndStatusIsActiveOrElseThrow(itemCreateRequest.getCategoryId());

        Item item = itemRepository.save(
                new Item(
                        itemCreateRequest.getTitle(),
                        itemCreateRequest.getDescription(),
                        itemCreateRequest.getPrice(),
                        user,
                        category
                )
        );

        List<ItemImage> images = itemImageRepository.findByUserIdAndItemIsNull(authUser.getId());
        item.fetchImage(images);
        images.forEach(image -> image.fetchItem(item));

        return ItemResponse.builder()
                .title(item.getTitle())
                .price(item.getPrice())
                .itemSaleStatus(item.getItemSaleStatus())
                .nickname(user.getNickname())
                .build();
    }

    /**
     * 매물의 판매 상태만을 변경하는 로직
     *
     * @param itemId         Item's ID
     * @param itemSaleStatus 판매 상태
     * @param authUser       매물 판매 상태 수정을 요청한 사용자
     */
    @Transactional
    public void updateSaleStatus(Long itemId, ItemSaleStatus itemSaleStatus, CustomUserDetails authUser) {
        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = itemRepository.findByIdAndSellerIdOrElseThrow(itemId, User.fromAuthUser(authUser).getId());

        item.changeSaleStatus(itemSaleStatus);
    }

    /**
     * 매물의 내용(제목, 설명, 가격)을 수정하는 로직
     *
     * @param itemId                    Item's ID
     * @param itemContentsUpdateRequest 매물 수정 정보를 포함한 요청 객체(제목, 내용, 가격, 이미지URL)
     * @param authUser                  매물 내용 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 설명, 가격, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse updateContents(Long itemId, ItemContentsUpdateRequest itemContentsUpdateRequest, CustomUserDetails authUser) {
        Item item = itemRepository.findByIdAndSellerIdOrElseThrow(itemId, User.fromAuthUser(authUser).getId());

        int oldPrice = item.getPrice(); // 이전 가격

        item.changeContents(
                itemContentsUpdateRequest.getTitle(),
                itemContentsUpdateRequest.getDescription(),
                itemContentsUpdateRequest.getPrice()
        );

        // flush()를 호출하여 변경 사항을 데이터베이스에 반영
        entityManager.flush();

        int newPrice = item.getPrice(); // 이제 변경된 가격을 가져옴

        // 로그 추가: 가격 변경 감지
        log.info("Updated Item ID: {}, Old Price: {}, New Price: {}", itemId, oldPrice, newPrice);

        // 가격이 변경된 경우 이벤트 발행
        if (oldPrice != newPrice) {
            eventPublisher.publishEvent(new PriceChangeEvent(itemId, newPrice));
        }

        return ItemResponse.builder()
                .title(item.getTitle())
                .price(item.getPrice())
                .description(item.getDescription())
                .nickname(item.getSeller().getNickname())
                .build();
    }

    /**
     * 자신이 등록한 매물을 논리적 삭제하는 로직
     *
     * @param itemId   Item's ID
     * @param authUser 매물 삭제를 요청한 사용자
     */
    @Transactional
    public void softDeleteItem(Long itemId, CustomUserDetails authUser) {
        Item item = itemRepository.findByIdAndSellerIdOrElseThrow(itemId, User.fromAuthUser(authUser).getId());

        item.solfDelete(Status.DELETED);
    }

    /**
     * 관리자가 신고된 매물을 논리적 삭제하는 로직
     *
     * @param itemId Item's ID
     */
    @Transactional
    public void softDeleteReportedItem(Long itemId) {
        Item item = itemRepository.findByIdOrElseThrow(itemId);

        item.solfDelete(Status.DELETED);
    }

    /**
     * 로그인 한 사용자가 특정 매물을 상세조회하는 로직
     *
     * @param itemId Item's ID
     * @return ItemResponseDto - Item에 있는 모든 정보값을 포함한 응답 객체
     */
    public ItemResponseDto getItem(Long itemId, CustomUserDetails authUser) {
        Item item = itemRepository.findByIdOrElseThrow(itemId);

        incrementViewCount(itemId, authUser.getId());

        return ItemResponseDto.from(item);
    }

    /**
     * 현재 인증된 사용자가 올린 모든 매물을 조회하는 로직
     *
     * @param page     페이지 번호(1부터 시작)
     * @param size     페이지당 카드 수
     * @param authUser 현재 인증된 사용자 정보
     * @return Page<ItemResponseDto> - 요청된 페이지에 해당하는 현재 인증된 사용자의 매물 목록을 포함한 페이지 정보
     * 각 매물은 ItemResponseDto 형태로 변환되어 반환됨
     * 매물들의 상세 정보와 페이지 정보를 포함하고 있음
     */
    public Page<ItemResponseDto> getMyItems(int page, int size, CustomUserDetails authUser) {
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());

        Pageable pageable = PageRequest.of(page - 1, size);

        return itemRepository.findBySeller(pageable, user).map(ItemResponseDto::from);
    }

    /**
     * 특정 카테고리에 모든 매물을 조회
     *
     * @param requestDto 페이지 번호와 페이지당 매물 수를 포함하는 요청 객체
     * @param categoryId Category's ID
     * @return Page<ItemResponseDto> - 요청된 페이지에 해당하는 특정 카테고리의 매물 목록을 포함한 페이지 정보
     * *          각 매물은 ItemResponseDto 형태로 변환되어 반환됨
     * *          매물들의 상세 정보와 페이지 정보를 포함하고 있음
     */
    public Page<ItemResponseDto> getCategoryItems(FindItemsInMyAreaRequestDto requestDto, Long categoryId, CustomUserDetails authUser) {
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        // 카테고리 존재 확인
        Category category = categoryRepository.findByIdOrElseThrow(categoryId);

        // 반경 5km 행정동 이름 반환
        List<String> areaList = admAreaService.getAdmNameListByAdmName(user.getAddress());

        Pageable pageable = createPageable(requestDto);

        Page<Item> result = itemRepository.findItemByAreaAndCategory(pageable, areaList, category.getId());

        return result.map(ItemResponseDto::from);
    }

    /**
     * 내 주변에 있는 매물을 조회하는 메서드입니다.
     * 추후 논의를 통해 다른 조회 시에도 해당 기능을 합칠 예정입니다.
     *
     * @param authUser   : 현재 인증받은 사용자
     * @param requestDto : 페이지번호, 크기가 들어있는 requestDto, @valid 사용하기 위해 파라미터로 안 받고 DTO 로 받았습니다.
     * @return 페이징해서 찾은 매물들 DTO 로 매핑해서 반환
     */
    public Page<ItemResponseDto> findItemsByMyArea(CustomUserDetails authUser, FindItemsInMyAreaRequestDto requestDto) {
        //User.fromAuthUser 쓰면 User 안에 있는 Address를 못 불러와서 이렇게 꺼냈습니다.
        User currentUser = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        String myArea = currentUser.getAddress();

        List<String> areaList = admAreaService.getAdmNameListByAdmName(myArea);
        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, requestDto.getSize());
        Page<Item> result = itemRepository.findByAreaListAndUserArea(pageable, areaList);

        return result.map(ItemResponseDto::from);
    }

    public List<ItemResponseDto> getTopItems(CustomUserDetails authUser) {
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        List<String> areaList = admAreaService.getAdmNameListByAdmName(user.getAddress());
        List<Item> items = itemRepository.findByUserArea(areaList);

        // Redis에서 조회수를 가져와 정렬하기 위해, 아이템과 조회수를 Map에 저장
        List<ItemWithViewCount> itemWithViewCounts = items.stream()
                .map(item -> {
                    // Redis에서 조회수 가져오기
                    Long viewCount = redisTemplate.opsForValue().get("ViewCount:ItemId:" + item.getId());
                    Long finalViewCount = (viewCount != null) ? viewCount : 0L; // 조회수가 null일 경우 0으로 설정

                    // 로그로 아이템별 조회수 출력
                    log.info("Item ID: {}, Title: {}, View Count: {}", item.getId(), item.getTitle(), finalViewCount);

                    return new ItemWithViewCount(item, finalViewCount);
                })
                .sorted(Comparator.comparingLong(ItemWithViewCount::getViewCount).reversed()) // 조회수 내림차순 정렬
                .limit(5) // 상위 5개 선택
                .toList();

        // ItemWithViewCount를 ItemResponseDto로 변환하여 반환
        return itemWithViewCounts.stream()
                .map(itemWithViewCount -> ItemResponseDto.from(itemWithViewCount.getItem()))
                .toList();
    }

    @Transactional
    public void boostItem(Long itemId, CustomUserDetails authUser) {
        Item item = itemRepository.findByIdOrElseThrow(itemId);

        String boostKey = String.format("boost:item:%d:user:%d", itemId, authUser.getId());

        // 하루에 한 번 부스트 제한
        Boolean isAlreadyBoosted = redisTemplate.hasKey(boostKey);
        if (Boolean.TRUE.equals(isAlreadyBoosted)) {
            throw new ApiException(ErrorStatus.CONFLICT_ITEM_BOOST);
        }

        // 부스트 처리 및 Redis에 키 저장 (1일 동안 유효)
        item.boostItem();
        redisTemplate.opsForValue().set(boostKey, 1L, 24, TimeUnit.HOURS);
    }

    private void incrementViewCount(Long itemId, Long userId) {
        String redisKey = "ViewCount:ItemId:" + itemId;
        String userKey = "UserView:ItemId:" + itemId + ":UserId:" + userId;

        // 사용자가 이미 조회했는지 확인
        Boolean hasViewed = redisTemplate.hasKey(userKey);

        // 사용자가 조회하지 않은 경우
        if (hasViewed == null || !hasViewed) {
            // 조회수 증가
            redisTemplate.opsForValue().increment(redisKey);
            // 사용자의 조회 기록을 1시간 후 만료되도록 설정
            redisTemplate.opsForValue().set(userKey, 1L, 60, TimeUnit.MINUTES);
        }
    }

    /**
     * 관심 상품으로 등록한 사용자들에게 가격 변경 알림을 보내는 메서드
     */
    private void notifyUsersAboutPriceChange(Long itemId, int newPrice) {
        // 관심 상품 사용자 조회
        List<User> interestedUsers = interestedItemService.findUsersByInterestedItem(itemId);

        // 관심 사용자들에게 알림 전송
        for (User user : interestedUsers) {
            simpMessagingTemplate.convertAndSend(String.format("/sub/user/%d/notifications", user.getId()),
                    String.format("관심 상품의 가격이 변경되었습니다. 새로운 가격 : %d", newPrice));
        }
    }

    /**
     * 관심 카테고리에 새로운 물품이 등록되었을 때 사용자에게 알림을 보내는 메서드
     */
    private void notifyUsersAboutNewItem(Long categoryId, String itemTitle) {
        List<User> interestedUsers = interestedCategoryService.findUsersByInterestedCategory(categoryId);
        for (User user : interestedUsers) {
            simpMessagingTemplate.convertAndSend("/sub/user/" + user.getId() + "/notifications",
                    "새로운 물품이 관심 카테고리에 등록되었습니다: " + itemTitle);
        }
    }

    // ItemSaleStatus 결정 메서드
    //이거 BooleanExpression 같은 개념이라 쿼리 DSL 구현체로 가야할 듯 싶습니당
    private ItemSaleStatus setSaleStatus(ItemSearchRequest itemSearchRequest) {
        return itemSearchRequest.isSaleStatus() ? ItemSaleStatus.WAITING : null;
    }

    // 알림 전송(가격 변동)
    private void sendPriceChangeNotification(Item item, int newPrice) {
        if (item.getPrice() != newPrice) {
            notifyUsersAboutPriceChange(item.getId(), newPrice);
        }
    }

    private Pageable createPageable(FindItemsInMyAreaRequestDto requestDto) {
        return PageRequest.of(requestDto.getPage() - 1, requestDto.getSize());
    }
}
