package com.sprarta.sproutmarket.domain.image.profileImage.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.profileImage.dto.ProfileImageResponse;
import com.sprarta.sproutmarket.domain.image.profileImage.entity.ProfileImage;
import com.sprarta.sproutmarket.domain.image.profileImage.repository.ProfileImageRepository;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {
    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    // 프로필 사진 추가
    @Transactional
    public ProfileImageResponse uploadProfileImage(ImageNameRequest imageName, CustomUserDetails authUser){
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());

        ProfileImage image = new ProfileImage(
            user,
            imageName.getImageName()
        );
        profileImageRepository.save(image);
        ProfileImageResponse profileImageResponse = new ProfileImageResponse(image.getName());
        return profileImageResponse;
    }

    // 프로필 사진 삭제
    @Transactional
    public void deleteProfileImage(ImageNameRequest imageName, CustomUserDetails authUser){
        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        profileImageRepository.findByUserOrElseThrow(user);
        s3ImageService.deleteImage(imageName.getImageName(), authUser);
        profileImageRepository.deleteByName(imageName.getImageName());
    }

}
