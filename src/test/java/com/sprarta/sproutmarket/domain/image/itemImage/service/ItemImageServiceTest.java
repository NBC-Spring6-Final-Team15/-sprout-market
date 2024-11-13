//package com.sprarta.sproutmarket.domain.image.itemImage.service;
//
//import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
//import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
//import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
//import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
//import com.sprarta.sproutmarket.domain.item.entity.Item;
//import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
//import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
//import com.sprarta.sproutmarket.domain.user.entity.User;
//import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import static org.mockito.Mockito.*;
//
//class ItemImageServiceTest {
//
//    @InjectMocks
//    private ItemImageService itemImageService;
//
//    @Mock
//    private ItemImageRepository itemImageRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ItemRepository itemRepository;
//
//    @Mock
//    private S3ImageService s3ImageService;
//
//    @Mock
//    private CustomUserDetails mockAuthUser;
//
//    private User mockUser;
//    private Item mockItem;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this); // Mock 초기화
//
//        mockUser = new User("testUser", "test@test.com", "password", "nickname", "010-1234-5678", "주소", null);
//        mockItem = new Item("title", "description", 1000, mockUser, null);
//
//        // mockAuthUser.getId() 설정
//        when(mockAuthUser.getId()).thenReturn(1L);
//    }
//
//    @Test
//    @DisplayName("매물 이미지 삭제 성공")
//    void deleteItemImage_success() {
//        Long itemId = 1L;
//        String imageName = "itemImage.jpg";
//        ImageNameRequest request = new ImageNameRequest(imageName);
//        ItemImage mockImage = new ItemImage(imageName, mockItem);
//
//        // Mock 동작 설정
//        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(1L)).thenReturn(mockUser);
//        when(itemRepository.findByIdAndSellerIdOrElseThrow(itemId, mockUser.getId())).thenReturn(mockItem);
//        when(itemImageRepository.findByNameOrElseThrow(imageName)).thenReturn(mockImage);
//
//        doNothing().when(s3ImageService).deleteImage(imageName, mockAuthUser);
//        doNothing().when(itemImageRepository).deleteById(mockImage.getId());
//
//        // 테스트 실행
//        itemImageService.deleteItemImage(itemId, request, mockAuthUser);
//
//        // 검증
//        verify(userRepository, times(1)).findByIdAndStatusIsActiveOrElseThrow(1L);
//        verify(itemRepository, times(1)).findByIdAndSellerIdOrElseThrow(itemId, mockUser.getId());
//        verify(s3ImageService, times(1)).deleteImage(imageName, mockAuthUser);
//        verify(itemImageRepository, times(1)).deleteById(mockImage.getId());
//    }
//}