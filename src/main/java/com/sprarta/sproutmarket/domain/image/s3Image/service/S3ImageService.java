package com.sprarta.sproutmarket.domain.image.s3Image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.util.Objects;
import java.util.UUID;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ImageService {
    private final AmazonS3 amazonS3;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    @Value("${s3.bucketName}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;  // 최대용량: 5MB

    public String checkUser(Long itemId, MultipartFile image, CustomUserDetails authUser){
        User user = findUserById(authUser.getId());
        verifyItemOwnership(itemId, user);
        return uploadImage(image, authUser);
    }

    public String uploadImage(MultipartFile image, CustomUserDetails authUser) {
        // 빈 파일 검증
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new ApiException(ErrorStatus.EMPTY_FILE_EXCEPTION);
        }
        // 파일 크기 검사
        if (image.getSize() > MAX_FILE_SIZE){
            throw new ApiException(ErrorStatus.FILE_SIZE_EXCEEDED);
        }
        // 파일 이름 생성
        String fileName = "user-uploads/" + authUser.getId() + "/" + UUID.randomUUID() + "_" + image.getOriginalFilename();
        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        try {
            // S3에 이미지 업로드
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), metadata));
            // S3에서 반환한 public URL
            String publicUrl = amazonS3.getUrl(bucketName, fileName).toString();
            return publicUrl;
        } catch (Exception e) {
            log.error("Failed to upload image to S3", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public void deleteImage(String imageName, CustomUserDetails authUser) {
        // S3에 저장된 파일 이름 생성
        String fileName = imageName.replace("https://sprout-market.s3.ap-northeast-2.amazonaws.com/", "");
        try {
            // S3에서 이미지 삭제
            amazonS3.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            log.error("Failed to delete image from S3", e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
    }

    private Item verifyItemOwnership(Long itemId, User user){
        return itemRepository.findByIdAndSellerIdOrElseThrow(itemId, user);
    }
}