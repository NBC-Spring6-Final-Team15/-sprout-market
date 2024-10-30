package com.sprarta.sproutmarket.domain.image.service;

import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile image, Long itemId, CustomUserDetails authUser);
    void deleteImage(String imageAddress);
}
