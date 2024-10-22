package com.sprarta.sproutmarket.domain.image.repository;

import com.sprarta.sproutmarket.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
