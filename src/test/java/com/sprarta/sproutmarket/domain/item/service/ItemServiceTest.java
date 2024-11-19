package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
import com.sprarta.sproutmarket.domain.image.itemImage.service.ItemImageService;
import com.sprarta.sproutmarket.domain.interestedCategory.service.InterestedCategoryService;
import com.sprarta.sproutmarket.domain.interestedItem.service.InterestedItemService;
import com.sprarta.sproutmarket.domain.item.dto.request.FindItemsInMyAreaRequestDto;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemSearchRequest;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepositoryCustom;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemImageRepository itemImageRepository;
    @Mock
    private ItemRepositoryCustom itemRepositoryCustom;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ItemImageService itemImageService;
    @Mock
    private AdministrativeAreaService admAreaService;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private InterestedItemService interestedItemService;
    @Mock
    private InterestedCategoryService interestedCategoryService;
    @Mock
    private RedisTemplate<String, Long> viewCountRedisTemplate; // RedisTemplate Mock

    @Mock
    private ValueOperations<String, Long> valueOperations;
    @InjectMocks
    private ItemService itemService;

    private User mockUser;
    private Category mockCategory1;
    private Item mockItem1;
    private Item mockItem2;
    private CustomUserDetails authUser;
    private FindItemsInMyAreaRequestDto requestDto;

    @BeforeEach
        // 코드 실행 전 작동 + 테스트 환경 초기화
    void setup() {
        MockitoAnnotations.openMocks(this); //어노테이션 Mock과 InjectMocks를 초기화

        // 가짜 사용자 생성
        //String username, String email, String password, String nickname, String phoneNumber, String address
        mockUser = new User(
                "가짜 객체1",
                "mock@mock.com",
                "Mock1234!",
                "오만한천원",
                "01012341234",
                "서울시 관악구 신림동",
                UserRole.USER
        );
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        // 가짜 관리자 생성
        User mockAdmin = new User(
                "가짜 관리자",
                "admin@admin.com",
                "Mock1234!",
                "관리자 A씨",
                "01012341234",
                "서울시 관악구 봉천동",
                UserRole.ADMIN
        );
        ReflectionTestUtils.setField(mockAdmin, "id", 1L);

        // 가짜 카테고리 생성
        mockCategory1 = new Category("생활");
        ReflectionTestUtils.setField(mockCategory1, "id", 1L);
        Category mockCategory2 = new Category("가구");
        ReflectionTestUtils.setField(mockCategory2, "id", 2L);

        // 가짜 매물 생성
        mockItem1 = new Item(
                "가짜 매물1",
                "가짜 설명1",
                10000,
                mockUser,
                mockCategory1
        );
        ReflectionTestUtils.setField(mockItem1, "id", 1L);


        mockItem2 = new Item(
                "가짜 매물2",
                "가짜 설명2",
                3000,
                mockUser,
                mockCategory2

        );
        ReflectionTestUtils.setField(mockItem2, "id", 2L);

        requestDto = FindItemsInMyAreaRequestDto.builder()
                .page(1)
                .size(10)
                .build();

        ItemImage itemImage = new ItemImage("https://sprout-market.s3.ap-northeast-2.amazonaws.com/4da210e1-7.jpg",mockUser);
        ReflectionTestUtils.setField(itemImage, "id", 1L);

        List<ItemImage> itemImageList = List.of(itemImage);

        mockItem1.fetchImage(itemImageList);
        mockItem2.fetchImage(itemImageList);

        authUser = new CustomUserDetails(
                mockUser
        );

        // CustomUserDetails(사용자 정보) 모킹 => 로그인된 사용자의 정보 모킹
        authUser = mock(CustomUserDetails.class);
        when(authUser.getId()).thenReturn(mockUser.getId()); // authUser의 ID를 mockUser의 ID로 설정
        when(authUser.getEmail()).thenReturn(mockUser.getEmail());
        when(authUser.getRole()).thenReturn(mockUser.getUserRole());

        // itemRepository.save() 호출 시 mockItem2를 반환하도록 설정
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem2);

        // userRepository.findById() 호출 시 mockUser를 반환하도록 설정
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // itemRepository.findById() 호출 시 mockItem1과 mockItem2를 반환하도록 설정
        when(itemRepository.findById(mockItem1.getId())).thenReturn(Optional.of(mockItem1));
        when(itemRepository.findById(mockItem2.getId())).thenReturn(Optional.of(mockItem2));

        when(itemImageRepository.findByIdOrElseThrow(itemImage.getId())).thenReturn(itemImage);

        when(viewCountRedisTemplate.opsForValue()).thenReturn(valueOperations);

        when(categoryRepository.findByIdAndStatusIsActiveOrElseThrow(mockCategory1.getId())).thenReturn(mockCategory1);
    }

    @Test
    @DisplayName("매물 단건 상세 조회 성공")
    void getItem_success() {
        Long itemId = 1L;
        // Given
        when(itemRepository.findByIdOrElseThrow(itemId)).thenReturn(mockItem1);

        // When
        ItemResponseDto result = itemService.getItem(itemId, authUser);

        // Then
        assertEquals(mockItem1.getId(), result.getId());
        assertEquals(mockItem1.getTitle(), result.getTitle());
        assertEquals(mockItem1.getDescription(), result.getDescription());
        assertEquals(mockItem1.getPrice(), result.getPrice());
        assertEquals(mockItem1.getSeller().getNickname(), result.getNickname());
        assertEquals(mockItem1.getItemSaleStatus(), result.getItemSaleStatus());
        assertEquals(mockItem1.getCategory().getName(), result.getCategoryName());
        assertEquals(mockItem1.getStatus(), result.getStatus());

        // Increment view count 검증
        verify(viewCountRedisTemplate.opsForValue(), times(1)).increment("ViewCount:ItemId:" + itemId);
    }

    @Test
    @DisplayName("매물 검색 성공")
    void searchItems_success() {
        // Given
        int page = 1;
        int size = 10;
        ItemSearchRequest itemSearchRequest = ItemSearchRequest.builder()
                .searchKeyword("타이틀")
                .categoryId(1L)
                .saleStatus(true)
                .build();

        List<String> areaList = List.of("서울시 관악구 신림동", "서울시 관악구 봉천동");
        Pageable pageable = PageRequest.of(0, size);

        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(mockUser.getId())).thenReturn(mockUser);
        when(admAreaService.getAdmNameListByAdmName("서울시 관악구 신림동")).thenReturn(areaList);
        when(categoryRepository.findByIdAndStatusIsActiveOrElseThrow(itemSearchRequest.getCategoryId())).thenReturn(mockCategory1);

        // When
        itemService.searchItems(page, size, itemSearchRequest, authUser);

        // Then
        verify(userRepository, times(1)).findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        verify(admAreaService, times(1)).getAdmNameListByAdmName("서울시 관악구 신림동");
        verify(categoryRepository, times(1)).findByIdAndStatusIsActiveOrElseThrow(itemSearchRequest.getCategoryId());
        verify(itemRepositoryCustom, times(1)).searchItems(areaList, itemSearchRequest.getSearchKeyword(), mockCategory1, ItemSaleStatus.WAITING, pageable);
    }

    @Test
    @DisplayName("매물 생성 성공")
    void addItem_success() {
        // Given
        ItemCreateRequest itemCreateRequest = ItemCreateRequest.builder()
                .title("가짜 매물1")
                .description("가짜 설명1")
                .price(10000)
                .categoryId(mockCategory1.getId())
                .imageName("test.jpg")
                .build();

        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(any())).thenReturn(mockUser);
        when(categoryRepository.findByIdAndStatusIsActiveOrElseThrow(mockCategory1.getId())).thenReturn(mockCategory1);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem1);

        // When
        ItemResponse itemResponse = itemService.addItem(itemCreateRequest, authUser);

        // Then
        assertEquals("가짜 매물1", itemResponse.getTitle());
        assertEquals(10000, itemResponse.getPrice());
        assertEquals("오만한천원", mockUser.getNickname());
    }

    @Test
    @DisplayName("매물 판매상태 변경 성공")
    void updateSaleStatus_success() {
        // Given
        when(itemRepository.findByIdAndSellerIdOrElseThrow(mockItem2.getId(), mockUser.getId())).thenReturn(mockItem2);

        // When
        itemService.updateSaleStatus(mockItem2.getId(), ItemSaleStatus.SOLD, authUser);

        // Then
        assertEquals(ItemSaleStatus.SOLD,mockItem2.getItemSaleStatus());
    }

    @Test
    @DisplayName("매물 내용 변경 성공")
    void updateContents_success() {
        // Given
        ItemContentsUpdateRequest contentsUpdateRequest = ItemContentsUpdateRequest.builder()
                .title("변경된 제목")
                .description("변경된 설명")
                .price(5000)
                .build();

        // userRepository에서 mockUser를 반환하도록 설정
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        // findByIdAndSellerIdOrElseThrow 메서드가 mockItem2를 반환하도록 설정
        when(itemRepository.findByIdAndSellerIdOrElseThrow(mockItem2.getId(), mockUser.getId())).thenReturn(mockItem2);

        // When
        ItemResponse itemResponse = itemService.updateContents(mockItem2.getId(), contentsUpdateRequest, authUser);

        // Then
        assertEquals("변경된 제목", itemResponse.getTitle());
        assertEquals(5000, itemResponse.getPrice());
    }

    @Test
    @DisplayName("사용자의 매물 (논리적)삭제 성공")
    void softDeleteItem_success() {
        // Given
        when(itemRepository.findByIdAndSellerIdOrElseThrow(mockItem1.getId(), mockUser.getId())).thenReturn(mockItem1);

        // When
        itemService.softDeleteItem(mockItem1.getId(), authUser);

        // Then
        assertEquals(Status.DELETED,mockItem1.getStatus());
    }


    @Test
    @DisplayName("관리자의 신고받은 매물 (논리적)삭제 성공")
    void softDeleteReportedItem_success() {
        // Given
        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);

        // When
        itemService.softDeleteReportedItem(mockItem1.getId());

        // Then
        assertEquals(Status.DELETED,mockItem1.getStatus());
    }

    @Test
    @DisplayName("자신의 매물 전체 조회 성공")
    void getMyItems_success() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Item> pageResult = new PageImpl<>(List.of(mockItem1), pageable, 1);

        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(mockUser.getId())).thenReturn(mockUser);
        when(itemRepository.findBySeller(pageable, mockUser)).thenReturn(pageResult);

        // When
        Page<ItemResponseDto> result = itemService.getMyItems(1, 10, authUser);

        // Then
        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).getNickname()).isEqualTo(mockItem1.getSeller().getNickname());
    }

    @Test
    @DisplayName("주변 매물 중 특정 카테고리에 속하는 매물 전체 조회 성공")
    void getCategoryItems_success() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Item> pageResult = new PageImpl<>(List.of(mockItem1), pageable, 1);

        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(mockUser.getId())).thenReturn(mockUser);
        when(categoryRepository.findByIdOrElseThrow(mockCategory1.getId())).thenReturn(mockCategory1);
        when(admAreaService.getAdmNameListByAdmName(mockUser.getAddress())).thenReturn(List.of("서울시 관악구 신림동", "서울시 관악구 봉천동"));
        when(itemRepository.findItemByAreaAndCategory(pageable, List.of("서울시 관악구 신림동", "서울시 관악구 봉천동"), mockCategory1.getId())).thenReturn(pageResult);

        // When
        Page<ItemResponseDto> result = itemService.getCategoryItems(requestDto, mockCategory1.getId(), authUser);

        // Then
        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).getCategoryName()).isEqualTo(mockItem1.getCategory().getName());
    }

    @Test
    @DisplayName("주변 매물 전체 조회 성공")
    void findItemsByMyArea_success() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Item> pageResult = new PageImpl<>(List.of(mockItem1), pageable, 1);

        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(mockUser.getId())).thenReturn(mockUser);
        when(admAreaService.getAdmNameListByAdmName(mockUser.getAddress())).thenReturn(List.of("서울시 관악구 신림동"));
        when(itemRepository.findByAreaListAndUserArea(pageable, List.of("서울시 관악구 신림동"))).thenReturn(pageResult);

        // When
        Page<ItemResponseDto> result = itemService.findItemsByMyArea(authUser, requestDto);

        // Then
        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).getCategoryName()).isEqualTo(mockItem1.getCategory().getName());
    }

}