package com.sprarta.sproutmarket.domain.image.profileImage.repository;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.image.profileImage.entity.ProfileImage;
import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    @Query("SELECT p FROM ProfileImage p WHERE p.user = :user")
    Optional<ProfileImage> findByUser(@Param("user") User user);

    default ProfileImage findByUserOrElseThrow(User user) {
        return findByUser(user)
            .orElseThrow(() -> new ApiException(ErrorStatus.FORBIDDEN_PROFILE_UPDATE));
    }

    @Query("SELECT i FROM ProfileImage i WHERE i.name = :imageAddress")
    Optional<ProfileImage> findByName(@Param("imageAddress") String imageAddress);

    default ProfileImage findByNameOrElseThrow(String imageAddress) {
        return findByName(imageAddress)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_IMAGE));
    }

    void deleteByName(String imageName);
}
