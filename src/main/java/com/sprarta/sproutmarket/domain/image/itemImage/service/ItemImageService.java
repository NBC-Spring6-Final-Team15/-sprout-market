package com.sprarta.sproutmarket.domain.image.itemImage.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.item.entity.Item;
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
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public String uploadItemImage(Long itemId, String imageName, CustomUserDetails authUser) {
        User user = userRepository.findById(authUser.getId())
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
        verifyItemOwnership(itemId, user);
        // 데이터베이스에 이미지 정보 저장
        ItemImage itemImage = ItemImage.builder()
            .name(imageName)
            .item(itemRepository.findByIdOrElseThrow(itemId)) // ItemRepository에서 Item 조회
            .build();

        ItemImage image = itemImageRepository.save(itemImage); // 이미지 정보 저장
        return image.getName(); // 업로드된 이미지 URL 반환
    }


    @Transactional
    public void deleteItemImage(Long itemId, String imageName, CustomUserDetails authUser) {
        User user = findUserById(authUser.getId());
        verifyItemOwnership(itemId, user);
        s3ImageService.deleteImage(imageName, authUser);
        itemImageRepository.deleteByName(imageName); // 이미지 정보 저장
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
    }

    private Item verifyItemOwnership(Long itemId, User user){
        return itemRepository.findByIdAndSellerIdOrElseThrow(itemId, user);
    }
}
