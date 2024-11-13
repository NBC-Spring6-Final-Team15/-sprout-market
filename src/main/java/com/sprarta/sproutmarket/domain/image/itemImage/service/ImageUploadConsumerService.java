//package com.sprarta.sproutmarket.domain.image.itemImage.service;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.S3Object;
//import com.sprarta.sproutmarket.config.RabbitMQConfig;
//import com.sprarta.sproutmarket.domain.image.dto.request.ImageUploadRequest;
//import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
//import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
//import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
//import com.sprarta.sproutmarket.domain.item.entity.Item;
//import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.coobird.thumbnailator.Thumbnails;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ImageUploadConsumerService {
//    private final ItemImageRepository itemImageRepository;
//    private final ItemRepository itemRepository;
//    private final S3ImageService s3ImageService;
//    private final AmazonS3 amazonS3;
//
//    @Value("${s3.bucketName}")
//    private String bucketName;
//
//    @Transactional
//    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
//    public void handleImageUploadRequest(ImageUploadRequest request) {
//        try {
//            log.info("Processing image upload request for itemId: {}, userId: {}", request.getItemId(), request.getUserId());
//
//            // 1. S3에서 원본 이미지 가져오기
//            S3Object s3Object = amazonS3.getObject(bucketName, request.getImageName());
//            BufferedImage originalImage = ImageIO.read(s3Object.getObjectContent());
//
//            // 2. 이미지 압축 및 재업로드
//            String compressedS3Key = compressAndUploadImage(originalImage, request);
//
//            // 3. 최종 S3 URL 생성 및 데이터베이스에 저장
//            String cdnUrl = s3ImageService.getS3FileUrl(compressedS3Key);
//            Item item = itemRepository.findByIdAndSellerIdOrElseThrow(request.getItemId(), request.getUserId());
//            itemImageRepository.save(new ItemImage(cdnUrl, item));
//            log.info("Image saved to item_image table for itemId: {}, URL: {}", request.getItemId(), cdnUrl);
//        } catch (Exception e) {
//            log.error("Failed to process image upload request", e);
//        }
//    }
//
//    private String compressAndUploadImage(BufferedImage originalImage, ImageUploadRequest request) throws Exception {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        Thumbnails.of(originalImage).size(800, 800).outputFormat("jpg").toOutputStream(outputStream);
//
//        String s3Key = "images/" + request.getUserId() + "/" + UUID.randomUUID() + "_" + request.getImageName();
//        s3ImageService.uploadCompressedImage(new ByteArrayInputStream(outputStream.toByteArray()), s3Key);
//        return s3Key;
//    }
//}