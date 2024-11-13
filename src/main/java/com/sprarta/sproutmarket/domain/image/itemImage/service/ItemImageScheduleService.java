package com.sprarta.sproutmarket.domain.image.itemImage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.itemImage.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemImageScheduleService {
    private final ItemImageRepository itemImageRepository;
    private final AmazonS3 amazonS3;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Scheduled(cron = "0 0 3 * * ?")
    private void deleteExpiredItemImages() {
        log.info("매물이 확정되지 않은 이미지 삭제");
        List<ItemImage> images = itemImageRepository.findByItemIsNullAndExpired();
        for (ItemImage image : images) {
            log.info("이미지 : {}",image.getName());
            amazonS3.deleteObject(bucketName,image.getName());
        }
        itemImageRepository.deleteAll(images);
    }
}
