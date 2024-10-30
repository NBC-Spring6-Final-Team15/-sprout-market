package com.sprarta.sproutmarket.domain.image.repository;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    void deleteByName(String imageAddress);

    Optional<Image> findByName(String imageAddress);

    default Image findByNameOrElseThrow(String imageAddress){
        return findByName(imageAddress)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_IMAGE));

    };

    default Image findByIdOrElseThrow(Long imageId){
        return findById(imageId)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_IMAGE));
    };
}
