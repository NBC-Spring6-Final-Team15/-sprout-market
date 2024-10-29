package com.sprarta.sproutmarket.domain.item.service;

import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String upload(MultipartFile image, Long itemId, CustomUserDetails authUser);
    void deleteImageFromS3(String imageAddress);
}
