package com.sprarta.sproutmarket.domain.image.profileImage.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.profileImage.entity.ProfileImage;
import com.sprarta.sproutmarket.domain.image.profileImage.repository.ProfileImageRepository;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
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
    public String uploadProfileImage(String imageName, CustomUserDetails authUser){
        User user = findUserById(authUser.getId());

        ProfileImage image = new ProfileImage(
            user,
            imageName
        );
        profileImageRepository.save(image);
        return image.getName();
    }

    // 프로필 사진 삭제
    @Transactional
    public void deleteProfileImage(String imageName, CustomUserDetails authUser){
        User user = findUserById(authUser.getId());
        profileImageRepository.findByUserOrElseThrow(user);
        s3ImageService.deleteImage(imageName, authUser);
        profileImageRepository.deleteByName(imageName);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
    }
}
