package com.sprarta.sproutmarket.domain.image.s3Image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sprarta.sproutmarket.config.RabbitMQConfig;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.dto.request.ImageUploadRequest;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageService {
    private final AmazonS3 amazonS3;
    private final RabbitTemplate rabbitTemplate;
    private final ItemImageRepository itemImageRepository;
    private final UserRepository userRepository;

    @Value("${s3.bucketName}")
    private String bucketName;

    public String uploadImage(MultipartFile image, CustomUserDetails authUser) {
        validateFile(image);
        String fileName = String.format("user-uploads/%d/%s_%s", authUser.getId(), UUID.randomUUID(),image.getOriginalFilename());
        return uploadToS3(fileName, image);
    }

    public void deleteImage(String imageName, CustomUserDetails authUser) {
        String fileName = imageName.replace("https://sprout-market.s3.ap-northeast-2.amazonaws.com/", "");
        amazonS3.deleteObject(bucketName, fileName);
    }

    @Async
    public CompletableFuture<String> uploadImageAsync(MultipartFile image, CustomUserDetails authUser) {
        validateFile(image);
        String fileName = String.format("item-images/%d/%s_%s",authUser.getId(),UUID.randomUUID(),image.getOriginalFilename());

        String publicUrl = uploadCompressedImageToS3(image, fileName);

        // RabbitMQ 메시지 발행
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, new ImageUploadRequest(fileName, authUser.getId()));
        log.info("Message sent to RabbitMQ queue for file: {}", fileName);

        User user = userRepository.findByIdAndStatusIsActiveOrElseThrow(authUser.getId());
        itemImageRepository.save(new ItemImage(fileName, user));

        return CompletableFuture.completedFuture(publicUrl);
    }

    // 압축된 이미지를 S3에 업로드하는 메서드
    public void uploadCompressedImage(InputStream inputStream, String s3Key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        amazonS3.putObject(bucketName, s3Key, inputStream, metadata);
    }

    // S3에서 파일 URL 을 가져오는 메서드
    public String getS3FileUrl(String s3Key) {
        return amazonS3.getUrl(bucketName, s3Key).toString();
    }

    /*
    파일 검사
    파일 명이 비어있거나, 파일이 비어있으면 예외 처리
    확장자가 이미지가 아니거나, 확장자가 없으면 예외 처리
     */
    private void validateFile(MultipartFile image) {
        String filename = image.getOriginalFilename();

        //파일 자체의 유효성 검사
        if (image.isEmpty() || Objects.isNull(filename)) {
            throw new ApiException(ErrorStatus.EMPTY_FILE_EXCEPTION);
        }

        //확장자 검사
        Set<String> allowedExtensions = Set.of("jpg", "png");
        int dotIndex = filename.lastIndexOf(".");

        if(dotIndex != -1) {
            String fileExtension = filename.substring(dotIndex+1).toLowerCase();
            if (!allowedExtensions.contains(fileExtension)) {
                throw new ApiException(ErrorStatus.INVALID_FILE_EXTENSION);
            }
        } else {
            throw new ApiException(ErrorStatus.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadToS3(String fileName, MultipartFile image) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), metadata));
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    private String uploadCompressedImageToS3(MultipartFile image, String fileName) {
        try (InputStream compressedInputStream = compressImage(image)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setContentLength(compressedInputStream.available());
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, compressedInputStream, metadata));
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload compressed image to S3", e);
        }
    }

    private InputStream compressImage(MultipartFile image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(bufferedImage).size(800, 800).outputFormat("jpg").toOutputStream(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}