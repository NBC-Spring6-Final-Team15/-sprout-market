package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.service.CategoryService;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.entity.Image;
import com.sprarta.sproutmarket.domain.image.repository.ImageRepository;
import com.sprarta.sproutmarket.domain.interestedCategory.service.InterestedCategoryService;
import com.sprarta.sproutmarket.domain.interestedItem.service.InterestedItemService;
import com.sprarta.sproutmarket.domain.item.dto.request.FindItemsInMyAreaRequestDto;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ImageService imageService;
    @Mock
    private AdministrativeAreaService admAreaService;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private InterestedItemService interestedItemService;
    @Mock
    private InterestedCategoryService interestedCategoryService;
    @InjectMocks
    private ItemService itemService;
    private User mockUser;
    private User mockAdmin;
    private Category mockCategory1;
    private Category mockCategory2;
    private Item mockItem1;
    private Item mockItem2;
    private CustomUserDetails authUser;
    private CustomUserDetails authAdmin;
    private FindItemsInMyAreaRequestDto requestDto;
    private Image image;
    private MockMultipartFile mockImage;

    @BeforeEach // 코드 실행 전 작동 + 테스트 환경 초기화
    void setup(){
        MockitoAnnotations.openMocks(this); //어노테이션 Mock과 InjectMocks를 초기화

        mockImage = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image content".getBytes());

        // 가짜 사용자 생성
        //String username, String email, String password, String nickname, String phoneNumber, String address, UserRole userRole
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
        mockAdmin = new User(
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
        mockCategory2 = new Category( "가구");

        // 가짜 매물 생성
        mockItem1 = Item.builder()
            .title("가짜 매물1")
            .description("가짜 설명1")
            .price(10000)
            .itemSaleStatus(ItemSaleStatus.WAITING)
            .seller(mockUser)
            .category(mockCategory1)
            .status(Status.ACTIVE)
            .build();
        ReflectionTestUtils.setField(mockItem1, "id", 1L);

        mockItem2 = Item.builder()
            .title("가짜 매물2")
            .description("가짜 설명2")
            .price(3000)
            .itemSaleStatus(ItemSaleStatus.WAITING)
            .seller(mockUser)
            .category(mockCategory2)
            .status(Status.ACTIVE)
            .build();
        ReflectionTestUtils.setField(mockItem2, "id", 2L);

        requestDto = new FindItemsInMyAreaRequestDto(1, 10);

        image = Image.builder()
            .id(1L)
            .item(mockItem1)
            .name("https://sprout-market.s3.ap-northeast-2.amazonaws.com/4da210e1-7.jpg")
            .build();


        // CustomUserDetails(사용자 정보) 모킹 => 로그인된 사용자의 정보 모킹
        authUser = mock(CustomUserDetails.class);
        when(authUser.getId()).thenReturn(mockUser.getId()); // authUser의 ID를 mockUser의 ID로 설정
        when(authUser.getEmail()).thenReturn("mock@mock.com");
        when(authUser.getRole()).thenReturn(UserRole.USER);

        // itemRepository.save() 호출 시 mockItem2를 반환하도록 설정
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem2);

        // userRepository.findById() 호출 시 mockUser를 반환하도록 설정
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // itemRepository.findById() 호출 시 mockItem1과 mockItem2를 반환하도록 설정
        when(itemRepository.findById(mockItem1.getId())).thenReturn(Optional.of(mockItem1));
        when(itemRepository.findById(mockItem2.getId())).thenReturn(Optional.of(mockItem2));
    }

    @Test
    void 매물_이미지_삭제_성공() {
        // Given
        Long itemId = 1L;
        Long imageId = 1L;

        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(itemRepository.findByIdAndSellerIdOrElseThrow(itemId, mockUser)).thenReturn(mockItem1);
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        // When
        ItemResponse itemResponse = itemService.deleteImage(itemId, authUser, imageId);

        // Then
        assertEquals(mockItem1.getTitle(), itemResponse.getTitle());
        assertEquals(mockItem1.getStatus(), itemResponse.getStatus());
        assertEquals(mockUser.getNickname(), itemResponse.getNickname());

        verify(userRepository, times(1)).findById(authUser.getId());
        verify(itemRepository, times(1)).findByIdAndSellerIdOrElseThrow(itemId, mockUser);
        verify(imageRepository, times(1)).findById(imageId);
        verify(imageRepository, times(1)).deleteById(imageId);
    }

    @Test
    void 매물_이미지_추가_성공() {
        // Given
        Long itemId = 1L;
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(itemRepository.findByIdAndSellerIdOrElseThrow(itemId, mockUser)).thenReturn(mockItem1);
        when(imageService.upload(any(MultipartFile.class), eq(itemId), eq(authUser))).thenReturn("image_url.jpg");

        Image savedImage = Image.builder().name("image_url.jpg").item(mockItem1).build();
        when(imageRepository.save(any(Image.class))).thenReturn(savedImage);

        // When
        ItemResponse itemResponse = itemService.addImage(itemId, authUser, mockImage);

        // Then
        assertEquals(mockItem1.getTitle(), itemResponse.getTitle());
        assertEquals(mockItem1.getStatus(), itemResponse.getStatus());
        assertEquals("image_url.jpg", itemResponse.getImageUrl());
        assertEquals(mockUser.getNickname(), itemResponse.getNickname());

        verify(userRepository, times(1)).findById(authUser.getId());
        verify(itemRepository, times(1)).findByIdAndSellerIdOrElseThrow(itemId, mockUser);
        verify(imageService, times(1)).upload(any(MultipartFile.class), eq(itemId), eq(authUser));
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    void 매물_생성_성공(){
        // Given
        ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
            "가짜 매물1",
            "가짜 설명1",
            10000,
                mockCategory1.getId()
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem1);
        when(categoryService.findByIdOrElseThrow(mockCategory1.getId())).thenReturn(mockCategory1);

        // When
        ItemResponse itemResponse = itemService.addItem(itemCreateRequest, authUser);

        // Then
        assertEquals("가짜 매물1", itemResponse.getTitle());
        assertEquals(10000, itemResponse.getPrice());
        assertEquals("오만한천원", itemResponse.getNickname());
    }

    @Test
    void 매물_판매상태_변경_성공() {
        // Given
        String newSaleStatus = "SOLD";

        // userRepository에서 mockUser를 반환하도록 설정
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        // itemRepository에서 mockItem2를 반환하도록 설정
        when(itemRepository.findById(mockItem2.getId())).thenReturn(Optional.of(mockItem2));
        // findByIdAndSellerIdOrElseThrow 메서드가 mockItem2를 반환하도록 설정
        when(itemRepository.findByIdAndSellerIdOrElseThrow(mockItem2.getId(), mockUser)).thenReturn(mockItem2);

        // When
        ItemResponse itemResponse = itemService.updateSaleStatus(mockItem2.getId(), newSaleStatus, authUser);

        // Then
        assertEquals("가짜 매물2", itemResponse.getTitle());
        assertEquals(3000, itemResponse.getPrice());
        assertEquals(ItemSaleStatus.SOLD, itemResponse.getItemSaleStatus());
        assertEquals("오만한천원", itemResponse.getNickname());
    }

    @Test
    void 매물_내용_변경_성공() {
        // Given
        ItemContentsUpdateRequest contentsUpdateRequest = new ItemContentsUpdateRequest(
            "변경된 제목",
            "변경된 설명",
            5000
        );

        // userRepository에서 mockUser를 반환하도록 설정
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        // findByIdAndSellerIdOrElseThrow 메서드가 mockItem2를 반환하도록 설정
        when(itemRepository.findByIdAndSellerIdOrElseThrow(mockItem2.getId(), mockUser)).thenReturn(mockItem2);

        // When
        ItemResponse itemResponse = itemService.updateContents(mockItem2.getId(), contentsUpdateRequest, authUser);

        // Then
        assertEquals("변경된 제목", itemResponse.getTitle());
        assertEquals(5000, itemResponse.getPrice());
    }

    @Test
    void 사용자_자신_매물_논리적_삭제_성공(){
        // Given
        // userRepository에서 mockUser를 반환하도록 설정
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        // findByIdAndSellerIdOrElseThrow 메서드가 mockItem2를 반환하도록 설정
        when(itemRepository.findByIdAndSellerIdOrElseThrow(mockItem1.getId(), mockUser)).thenReturn(mockItem1);

        // When
        ItemResponse itemResponse = itemService.softDeleteItem(mockItem1.getId(), authUser);

        // Then
        assertEquals("가짜 매물1", itemResponse.getTitle());
        assertEquals(10000, itemResponse.getPrice());
        assertEquals(Status.DELETED, itemResponse.getStatus());
        assertEquals("오만한천원", itemResponse.getNickname());
    }


    @Test
    void 관리자_신고매물_논리적_삭제_성공() {
        // Given
        when(userRepository.findById(mockAdmin.getId())).thenReturn(Optional.of(mockAdmin));
        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);
        // CustomUserDetails(사용자 정보) 모킹 => 로그인된 사용자의 정보 모킹
        authAdmin = mock(CustomUserDetails.class);
        when(authAdmin.getId()).thenReturn(mockAdmin.getId()); // authUser의 ID를 mockUser의 ID로 설정
        when(authAdmin.getEmail()).thenReturn(mockAdmin.getEmail());
        when(authAdmin.getRole()).thenReturn(mockAdmin.getUserRole());

        // When
        ItemResponse result = itemService.softDeleteReportedItem(mockItem1.getId(), authAdmin);

        // Then
        assertEquals("가짜 매물1", result.getTitle());
        assertEquals("가짜 설명1", result.getDescription());
        assertEquals(10000, result.getPrice());
        assertEquals(Status.DELETED, result.getStatus());
    }

    @Test
    void 관리자_신고매물_논리적_삭제_실패_권한없음() {
        // Given
        when(userRepository.findById(mockAdmin.getId())).thenReturn(Optional.of(mockAdmin));
        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);

        // CustomUserDetails(사용자 정보) 모킹 => 로그인된 사용자의 정보 모킹
        authAdmin = mock(CustomUserDetails.class);
        when(authAdmin.getId()).thenReturn(mockAdmin.getId()); // authUser의 ID를 mockUser의 ID로 설정
        when(authAdmin.getEmail()).thenReturn(mockAdmin.getEmail());
        when(authAdmin.getRole()).thenReturn(UserRole.USER); // ADMIN이 아닌 다른 역할 설정

        // When & Then
        ApiException thrown = assertThrows(ApiException.class, () -> {
            itemService.softDeleteReportedItem(mockItem1.getId(), authAdmin);
        });

        assertEquals(ErrorStatus.FORBIDDEN_TOKEN, thrown.getErrorCode()); // 예외의 상태가 FORBIDDEN_TOKEN인지 확인
    }


//    @Test
//    void 매물_단건_상세_조회_성공() {
//        // Given
//        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);
//
//        // When
//        ItemResponseDto result = itemService.getItem(mockItem1.getId());
//
//        // Then
//        assertEquals(mockItem1.getId(), result.getId());
//        assertEquals(mockItem1.getTitle(), result.getTitle());
//        assertEquals(mockItem1.getPrice(), result.getPrice());
//        assertEquals(mockItem1.getSeller().getNickname(), result.getNickname());
//        assertEquals(mockItem1.getCategory().getName(), result.getCategoryName());
//    }

    @Test
    void 자신매물_전체_조회_성공() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Item> pageResult = new PageImpl<>(List.of(mockItem1), pageable, 1);

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(itemRepository.findBySeller(pageable, mockUser)).thenReturn(pageResult);

        // When
        Page<ItemResponseDto> result = itemService.getMyItems(1, 10, authUser);

        // Then
        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).getNickname()).isEqualTo(mockItem1.getSeller().getNickname());
    }

    @Test
    void 근처_매물_중_특정카테고리_전체_조회_성공() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Item> pageResult = new PageImpl<>(List.of(mockItem1), pageable, 1);

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(categoryService.findByIdOrElseThrow(mockCategory1.getId())).thenReturn(mockCategory1);
        when(admAreaService.getAdmNameListByAdmName(mockUser.getAddress())).thenReturn(List.of("서울시 관악구 신림동", "서울시 관악구 봉천동"));
        when(itemRepository.findItemByAreaAndCategory(pageable, List.of("서울시 관악구 신림동", "서울시 관악구 봉천동"), mockCategory1.getId())).thenReturn(pageResult);

        // When
        Page<ItemResponseDto> result = itemService.getCategoryItems(requestDto, mockCategory1.getId(), authUser);

        // Then
        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).getCategoryName()).isEqualTo(mockItem1.getCategory().getName());
    }

    @Test
    void 로그인한_사용자_주변_매물_전체_조회_성공() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Item> pageResult = new PageImpl<>(List.of(mockItem1), pageable, 1);

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(admAreaService.getAdmNameListByAdmName(mockUser.getAddress())).thenReturn(List.of("서울시 관악구 신림동"));
        when(itemRepository.findByAreaListAndUserArea(pageable, List.of("서울시 관악구 신림동"))).thenReturn(pageResult);

        // When
        Page<ItemResponseDto> result = itemService.findItemsByMyArea(authUser, requestDto);

        // Then
        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).getCategoryName()).isEqualTo(mockItem1.getCategory().getName());
    }


}