//package com.sprarta.sproutmarket.domain.image.itemImage.service;
//
//import com.sprarta.sproutmarket.domain.category.entity.Category;
//import com.sprarta.sproutmarket.domain.image.dto.response.ImageResponse;
//import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
//import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
//import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
//import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
//import com.sprarta.sproutmarket.domain.item.entity.Item;
//import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
//import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
//import com.sprarta.sproutmarket.domain.user.entity.User;
//import com.sprarta.sproutmarket.domain.user.enums.UserRole;
//import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.verify;
//
//class ItemImageServiceTest {
//    @InjectMocks
//    private ItemImageService itemImageService;
//    @Mock
//    private ItemImageRepository itemImageRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private ItemRepository itemRepository;
//    @Mock
//    private S3ImageService s3ImageService;
//    @Mock
//    private CustomUserDetails mockAuthUser;
//
//    private User mockUser;
//    private Item mockItem;
//    private Category mockCategory;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockUser = new User(
//            "testUser",
//            "test@test.com",
//            "encodedOldPassword",
//            "testNickname",
//            "010-1234-5678",
//            "서울특별시 관악구 신림동",
//            UserRole.USER);
//        mockAuthUser = new CustomUserDetails(mockUser);
//        mockCategory = new Category("생활");
//        ReflectionTestUtils.setField(mockCategory, "id", 1L);
//
//        mockItem = new Item(
//            "test title",
//            "test description",
//            1000,
//            mockUser,
//            mockCategory
//        );
//    }
//
//    @Test
//    @DisplayName("매물 이미지 업로드 성공")
//    void uploadItemImage_success() {
//        // Given
//        Long itemId = 1L;
//        ImageNameRequest request = new ImageNameRequest("itemImage.jpg");
//        ItemImage image = new ItemImage(request.getImageName(), mockItem);
//        when(itemRepository.findByIdAndSellerIdOrElseThrow(itemId, mockUser.getId())).thenReturn(mockItem);
//        when(itemImageRepository.save(any(ItemImage.class))).thenReturn(image);
//
//        // when
//        ImageResponse response = itemImageService.uploadItemImage(itemId, request, mockAuthUser);
//
//        // then
//        assertNotNull(response);
//        assertEquals("itemImage.jpg", response.getName());
//        verify(itemRepository).findByIdAndSellerIdOrElseThrow(itemId, mockUser.getId());
//        verify(itemImageRepository).save(any(ItemImage.class));
//    }
//
//    @Test
//    @DisplayName("매물 이미지 삭제 성공")
//    void deleteItemImage_success() {
//        // given
//        Long itemId = 1L;
//        ImageNameRequest request = new ImageNameRequest("itemImage.jpg");
//        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(mockUser.getId())).thenReturn(mockUser);
//        when(itemRepository.findByIdAndSellerIdOrElseThrow(itemId, mockUser.getId())).thenReturn(mockItem);
//        doNothing().when(s3ImageService).deleteImage(anyString(), eq(mockAuthUser));
//        doNothing().when(itemImageRepository).deleteByName(anyString());
//
//        // when
//        itemImageService.deleteItemImage(itemId, request, mockAuthUser);
//
//        // then
//        verify(userRepository).findByIdAndStatusIsActiveOrElseThrow(mockUser.getId());
//        verify(itemRepository).findByIdAndSellerIdOrElseThrow(itemId, mockUser.getId());
//        verify(s3ImageService).deleteImage("itemImage.jpg", mockAuthUser);
//        verify(itemImageRepository).deleteByName("itemImage.jpg");
//    }
//
//}