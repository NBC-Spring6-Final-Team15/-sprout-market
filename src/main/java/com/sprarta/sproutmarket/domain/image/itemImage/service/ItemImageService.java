package com.sprarta.sproutmarket.domain.image.itemImage.service;

import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void deleteItemImage(Long itemId, ImageNameRequest request, CustomUserDetails authUser) {
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());

        verifyItemOwnership(itemId, user.getId());

        s3ImageService.deleteImage(request.getImageName(), authUser);
        ItemImage itemImage = itemImageRepository.findByNameOrElseThrow(request.getImageName());

        itemImageRepository.deleteById(itemImage.getId()); // 이미지 정보 저장
    }

    private Item verifyItemOwnership(Long itemId, Long userId){
        return itemRepository.findByIdAndSellerIdOrElseThrow(itemId, userId);
    }
}
