package com.sprarta.sproutmarket.domain.image.itemImage.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.dto.ImageResponse;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public List<ImageResponse> uploadItemImages(Long itemId, List<ImageNameRequest> requests, CustomUserDetails authUser) {
        Item item = verifyItemOwnership(itemId, User.fromAuthUser(authUser).getId());

        List<ImageResponse> imageResponses = new ArrayList<>();
        for (ImageNameRequest request : requests) {
            // 각 이미지 정보를 저장
            ItemImage itemImage = ItemImage.builder()
                    .name(request.getImageName())
                    .item(item)
                    .build();

            ItemImage image = itemImageRepository.save(itemImage);

            imageResponses.add(new ImageResponse(image.getName()));
        }
        return imageResponses;
    }


    @Transactional
    public void deleteItemImage(Long itemId, ImageNameRequest request, CustomUserDetails authUser) {
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());

        verifyItemOwnership(itemId, user.getId());

        s3ImageService.deleteImage(request.getImageName(), authUser);

        itemImageRepository.deleteByName(request.getImageName()); // 이미지 정보 저장
    }

    private Item verifyItemOwnership(Long itemId, Long userId){
        return itemRepository.findByIdAndSellerIdOrElseThrow(itemId, userId);
    }
}
