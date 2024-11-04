package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
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
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Transactional(readOnly = true)
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemRepositoryCustom itemRepositoryCustom;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AdministrativeAreaService admAreaService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final InterestedItemService interestedItemService;
    private final RedisTemplate<String, Long> viewCountRedisTemplate;
    private final InterestedCategoryService interestedCategoryService;

    /**
     * 중고 매물에 대해서 검색하는 로직
     * @param page 페이지당 카드 수
     * @param size 현재 인증된 사용자 정보
     * @param itemSearchRequest 매물 검색 조건을 포함한 요청 객체(키워드, 카테고리id, 판매상태)
     * @param authUser 매물 수정을 요청한 사용자
     * @return Page<ItemResponseDto> - 요청된 페이지에 해당하는 검색 조건에 맞는 매물 목록을 포함한 페이지 정보
     * 각 매물은 ItemResponseDto 형태로 변환되어 반환됨
     */
    public Page<ItemSearchResponse> searchItems(int page, int size, ItemSearchRequest itemSearchRequest, CustomUserDetails authUser){
        // 유저 조회
        User user = findUserById(authUser.getId());
        // 반경 5km 행정동 이름 반환
        List<String> areaList = admAreaService.getAdmNameListByAdmName(user.getAddress());
        Category category = categoryRepository.findByIdAndStatusIsActiveOrElseThrow(itemSearchRequest.getCategoryId());
        ItemSaleStatus itemSaleStatus = setSaleStatus(itemSearchRequest);

        Pageable pageable = PageRequest.of(page-1, size);

        return itemRepositoryCustom.searchItems(areaList, itemSearchRequest.getSearchKeyword(), category, itemSaleStatus, pageable);
    }

    /**
     * 로그인한 사용자가 중고 물품을 등록하는 로직
     * @param itemCreateRequest 매물 세부 정보를 포함한 요청 객체(제목, 설명, 가격, 카테고리id, 업로드한 이미지 이름)
     * @param authUser 매물 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 가격, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse addItem(ItemCreateRequest itemCreateRequest, CustomUserDetails authUser){
        User user = findUserById(authUser.getId());
        Category category = categoryRepository.findByIdAndStatusIsActiveOrElseThrow(itemCreateRequest.getCategoryId());
        Item item = new Item(
            itemCreateRequest.getTitle(),
            itemCreateRequest.getDescription(),
            itemCreateRequest.getPrice(),
            user,
            ItemSaleStatus.WAITING,
            category,
            Status.ACTIVE
        );
        Item saveItem = itemRepository.save(item);

        // itemImageService.uploadItemImage(item.getId(), itemCreateRequest.getImageName(), authUser);
        // 카테고리에 관심 있는 사용자들에게 알림 전송
        notifyCategorySubscribersForNewItem(item.getCategory().getId(), item.getTitle());

        return ItemResponse.builder()
            .title(saveItem.getTitle())
            .price(saveItem.getPrice())
            .nickname(user.getNickname())
            .build();
    }

    /**
     * 매물의 판매 상태만을 변경하는 로직
     * @param itemId Item's ID
     * @param itemSaleStatus 판매 상태
     * @param authUser 매물 판매 상태 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 가격, 판매상태, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse updateSaleStatus(Long itemId, String itemSaleStatus, CustomUserDetails authUser) {
        // response(user.email)를 위해 AuthUser에서 사용자 정보 가져오기
        User user = findUserById(authUser.getId());
        // 매물 존재하는지, 해당 유저의 매물이 맞는지 확인
        Item item = verifyItemOwnership(itemId, user);

        ItemSaleStatus newItemSaleStatus = ItemSaleStatus.of(itemSaleStatus);
        item.changeSaleStatus(newItemSaleStatus);

        return ItemResponse.builder()
            .title(item.getTitle())
            .price(item.getPrice())
            .itemSaleStatus(item.getItemSaleStatus())
            .nickname(user.getNickname())
            .build();
    }

    /**
     * 매물의 내용(제목, 설명, 가격)을 수정하는 로직
     * @param itemId Item's ID
     * @param itemContentsUpdateRequest 매물 수정 정보를 포함한 요청 객체(제목, 내용, 가격, 이미지URL)
     * @param authUser 매물 내용 수정을 요청한 사용자
     * @return ItemResponse - 등록된 매물의 제목, 설명, 가격, 등록한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse updateContents(Long itemId, ItemContentsUpdateRequest itemContentsUpdateRequest, CustomUserDetails authUser){
        User user = findUserById(authUser.getId());
        Item item = verifyItemOwnership(itemId, user);

        sendPriceChangeNotification(item, itemContentsUpdateRequest.getPrice());

        item.changeContents(
            itemContentsUpdateRequest.getTitle(),
            itemContentsUpdateRequest.getDescription(),
            itemContentsUpdateRequest.getPrice()
        );

        return ItemResponse.builder()
            .title(item.getTitle())
            .price(item.getPrice())
            .description(item.getDescription())
            .nickname(user.getNickname())
            .build();
    }

    /**
     * 자신이 등록한 매물을 논리적 삭제하는 로직
     * @param itemId Item's ID
     * @param authUser 매물 삭제를 요청한 사용자
     * @return ItemResponse - 삭제된 매물의 제목, 상태, 삭제한 사용자의 닉네임을 포함한 응답 객체
     */
    @Transactional
    public ItemResponse softDeleteItem(Long itemId, CustomUserDetails authUser){
        User user = findUserById(authUser.getId());
        Item item = verifyItemOwnership(itemId, user);

        item.solfDelete(
            Status.DELETED
        );

        return ItemResponse.builder()
            .title(item.getTitle())
            .price(item.getPrice())
            .status(item.getStatus())
            .nickname(user.getNickname())
            .build();
    }

    /**
     * 관리자가 신고된 매물을 논리적 삭제하는 로직
     * @param itemId Item's ID
     * @return ItemResponse - 삭제된 매물의 제목, 설명, 상태를 포함한 응답 객체
     */
    @Transactional
    public ItemResponse softDeleteReportedItem(Long itemId, CustomUserDetails authUser){
        if(!authUser.getRole().equals(UserRole.ADMIN)){
            throw new ApiException(ErrorStatus.FORBIDDEN_TOKEN);
        }

        Item item = findItemById(itemId);

        item.solfDelete(
            Status.DELETED
        );

        return ItemResponse.builder()
            .title(item.getTitle())
            .description(item.getDescription())
            .price(item.getPrice())
            .status(item.getStatus())
            .build();
    }

    /**
     * 로그인 한 사용자가 특정 매물을 상세조회하는 로직
     * @param itemId Item's ID
     * @return ItemResponseDto - Item에 있는 모든 정보값을 포함한 응답 객체
     */
    public ItemResponseDto getItem(Long itemId, CustomUserDetails authUser){
        Item item = findItemById(itemId);

        incrementViewCount(itemId, authUser.getId());

        return ItemResponseDto.from(item);
    }

    /**
     * 현재 인증된 사용자의 모든 매물을 조회하는 로직
     * @param page 페이지 번호(1부터 시작)
     * @param size 페이지당 카드 수
     * @param authUser 현재 인증된 사용자 정보
     * @return Page<ItemResponseDto> - 요청된 페이지에 해당하는 현재 인증된 사용자의 매물 목록을 포함한 페이지 정보
     *          각 매물은 ItemResponseDto 형태로 변환되어 반환됨
     *          매물들의 상세 정보와 페이지 정보를 포함하고 있음
     */
    public Page<ItemResponseDto> getMyItems(int page, int size, CustomUserDetails authUser){
        User user = findUserById(authUser.getId());

        return itemRepository.findBySeller(PageRequest.of(page - 1, size), user)
                .map(ItemResponseDto::from);
    }

    /**
     * 특정 카테고리에 모든 매물을 조회
     * @param requestDto 페이지 번호와 페이지당 매물 수를 포함하는 요청 객체
     * @param categoryId Category's ID
     * @return Page<ItemResponseDto> - 요청된 페이지에 해당하는 특정 카테고리의 매물 목록을 포함한 페이지 정보
     *      *          각 매물은 ItemResponseDto 형태로 변환되어 반환됨
     *      *          매물들의 상세 정보와 페이지 정보를 포함하고 있음
     */
    public Page<ItemResponseDto> getCategoryItems(FindItemsInMyAreaRequestDto requestDto, Long categoryId, CustomUserDetails authUser){
        User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
        // 카테고리 존재 확인
        categoryRepository.findByIdOrElseThrow(categoryId);

        return itemRepository.findItemByAreaAndCategory(
                        createPageable(requestDto),
                        getAreaListByUserAddress(user.getAddress()),
                        categoryId)
                .map(ItemResponseDto::from);
    }



    /**
     * 내 주변에 있는 매물을 조회하는 메서드입니다.
     * 추후 논의를 통해 다른 조회 시에도 해당 기능을 합칠 예정입니다.
     * @param authUser : 현재 인증받은 사용자
     * @param requestDto : 페이지번호, 크기가 들어있는 requestDto, @valid 사용하기 위해 파라미터로 안 받고 DTO 로 받았습니다.
     * @return 페이징해서 찾은 매물들 DTO 로 매핑해서 반환
     */
    public Page<ItemResponseDto> findItemsByMyArea (CustomUserDetails authUser, FindItemsInMyAreaRequestDto requestDto) {
        //User.fromAuthUser 쓰면 User 안에 있는 Address를 못 불러와서 이렇게 꺼냈습니다.
        User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        List<String> areaList = admAreaService.getAdmNameListByAdmName(user.getAddress());
        Pageable pageable = PageRequest.of(requestDto.getPage()-1, requestDto.getSize());

        return itemRepository.findByAreaListAndUserArea(pageable,areaList)
                .map(ItemResponseDto::from);
    }

    public List<ItemResponseDto> getTopItems(CustomUserDetails authUser) {
        User user = findUserById(authUser.getId());
        List<String> areaList = admAreaService.getAdmNameListByAdmName(user.getAddress());
        List<Item> items = itemRepository.findByUserArea(areaList);

        // Redis에서 조회수를 가져와 정렬하기 위해, 아이템과 조회수를 Map에 저장
        List<ItemWithViewCount> itemWithViewCounts = items.stream()
                .map(item -> {
                    // Redis에서 조회수 가져오기
                    Long viewCount = viewCountRedisTemplate.opsForValue().get("ViewCount:ItemId:" + item.getId());
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
                .map(itemWithViewCount -> new ItemResponseDto(
                        itemWithViewCount.getItem().getId(),
                        itemWithViewCount.getItem().getTitle(),
                        itemWithViewCount.getItem().getDescription(),
                        itemWithViewCount.getItem().getPrice(),
                        itemWithViewCount.getItem().getSeller().getNickname(),
                        itemWithViewCount.getItem().getItemSaleStatus(),
                        itemWithViewCount.getItem().getCategory().getName(),
                        itemWithViewCount.getItem().getStatus()
                ))
                .toList();
    }

    private void incrementViewCount(Long itemId, Long userId) {
        String redisKey = "ViewCount:ItemId:" + itemId;
        String userKey = "UserView:ItemId:" + itemId + ":UserId:" + userId;

        // 사용자가 이미 조회했는지 확인
        Boolean hasViewed = viewCountRedisTemplate.hasKey(userKey);

        // 사용자가 조회하지 않은 경우
        if (hasViewed == null || !hasViewed) {
            // 조회수 증가
            viewCountRedisTemplate.opsForValue().increment(redisKey);
            // 사용자의 조회 기록을 1시간 후 만료되도록 설정
            viewCountRedisTemplate.opsForValue().set(userKey, 1L, 60, TimeUnit.MINUTES);
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
            simpMessagingTemplate.convertAndSend("/sub/user/" + user.getId() + "/notifications",
                    "관심 상품의 가격이 변경되었습니다. 새로운 가격: " + newPrice);
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

    private Item findItemById(Long itemId){
        return itemRepository.findByIdOrElseThrow(itemId);
    }

    // 유저 조회 메서드 분리
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
    }


    // ItemSaleStatus 결정 메서드
    private ItemSaleStatus setSaleStatus(ItemSearchRequest itemSearchRequest) {
        return itemSearchRequest.isSaleStatus() ? ItemSaleStatus.WAITING : null;
    }

    // 알림 전송(매물 등록시 해당 카테고리)
    private void notifyCategorySubscribersForNewItem(Long categoryId, String title){
        notifyUsersAboutNewItem(categoryId, title);
    }

    private Item verifyItemOwnership(Long itemId, User user){
        return itemRepository.findByIdAndSellerIdOrElseThrow(itemId, user);
    }

    // 알림 전송(가격 변동)
    private void sendPriceChangeNotification(Item item, int newPrice){
        if (item.getPrice() != newPrice) {
            notifyUsersAboutPriceChange(item.getId(), newPrice);
        }
    }

    private List<String> getAreaListByUserAddress(String address){
        return admAreaService.getAdmNameListByAdmName(address);
    }

    private Pageable createPageable(FindItemsInMyAreaRequestDto requestDto){
        return PageRequest.of(requestDto.getPage()-1, requestDto.getSize());
    }
}
