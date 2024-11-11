package com.sprarta.sproutmarket.domain.image.s3Image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sprarta.sproutmarket.config.RabbitMQConfig;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.dto.request.ImageUploadRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageService {
    private final AmazonS3 amazonS3;
    private final RabbitTemplate rabbitTemplate;

    @Value("${s3.bucketName}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;  // 최대 5MB

    public String uploadImage(MultipartFile image, CustomUserDetails authUser) {
        validateFile(image);
        String fileName = generateFileName(authUser.getId(), image.getOriginalFilename());
        return uploadToS3(fileName, image);
    }

    public void deleteImage(String imageName, CustomUserDetails authUser) {
        String fileName = imageName.replace("https://sprout-market.s3.ap-northeast-2.amazonaws.com/", "");
        amazonS3.deleteObject(bucketName, fileName);
    }

    @Async
    public CompletableFuture<String> uploadImageAsync(Long itemId, MultipartFile image, CustomUserDetails authUser) {
        validateFile(image);
        String fileName = "item-images/" + itemId + "/" + UUID.randomUUID() + "_" + image.getOriginalFilename();
        String publicUrl = uploadCompressedImageToS3(image, fileName);

        // RabbitMQ 메시지 발행
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, new ImageUploadRequest(itemId, fileName, authUser.getId()));
        log.info("Message sent to RabbitMQ queue for file: {}", fileName);

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

    private void validateFile(MultipartFile image) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new ApiException(ErrorStatus.EMPTY_FILE_EXCEPTION);
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(ErrorStatus.FILE_SIZE_EXCEEDED);
        }
    }

    private String generateFileName(Long userId, String originalFilename) {
        return "user-uploads/" + userId + "/" + UUID.randomUUID() + "_" + originalFilename;
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