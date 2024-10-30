package com.sprarta.sproutmarket.domain.image.service.impl;

import com.sprarta.sproutmarket.domain.image.entity.Image;
import com.sprarta.sproutmarket.domain.image.repository.ImageRepository;
import com.sprarta.sproutmarket.domain.image.service.ImageService;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
public class ImageServiceImpl implements ImageService {
    private final S3ImageServiceImpl s3ImageServiceImpl; // S3ImageServiceImpl 의존성 주입
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;

    public ImageServiceImpl(S3ImageServiceImpl s3ImageServiceImpl, ImageRepository imageRepository, ItemRepository itemRepository) {
        this.s3ImageServiceImpl = s3ImageServiceImpl;
        this.imageRepository = imageRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public String uploadImage(MultipartFile image, Long itemId, CustomUserDetails authUser) {
        String imageAddress = s3ImageServiceImpl.uploadImage(image, itemId, authUser);

        // 데이터베이스에 이미지 정보 저장
        Image newImage = Image.builder()
            .name(imageAddress)
            .item(itemRepository.findByIdOrElseThrow(itemId)) // ItemRepository에서 Item 조회
            .build();

        imageRepository.save(newImage); // 이미지 정보 저장
        return imageAddress; // 업로드된 이미지 URL 반환
    }

    @Override
    public void deleteImage(Long itemId, CustomUserDetails authUser, String imageAddress) {
        s3ImageServiceImpl.deleteImage(itemId, authUser, imageAddress);
        imageRepository.deleteByName(imageAddress);
    }
}
