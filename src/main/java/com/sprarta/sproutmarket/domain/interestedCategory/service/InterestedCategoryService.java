package com.sprarta.sproutmarket.domain.interestedCategory.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory;
import com.sprarta.sproutmarket.domain.interestedCategory.repository.InterestedCategoryRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestedCategoryService {

    private final InterestedCategoryRepository interestedCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * 관심 카테고리 추가 로직
     * @param categoryId 카테고리 ID
     * @param authUser 인증된 사용자 정보
     */
    @Transactional
    public void addInterestedCategory(Long categoryId, CustomUserDetails authUser) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));

        // 이미 관심 카테고리에 등록되었는지 확인
        if (interestedCategoryRepository.existsByUserAndCategory(user, category)) {
            throw new ApiException(ErrorStatus.ALREADY_INTERESTED_CATEGORY);
        }

        // 생성자를 통해 InterestedCategory 객체 생성
        InterestedCategory interestedCategory = new InterestedCategory(user, category);

        interestedCategoryRepository.save(interestedCategory);
    }

    /**
     * 관심 카테고리 삭제 로직
     * @param categoryId 삭제할 카테고리 ID
     * @param authUser 인증된 사용자 정보
     */
    @Transactional
    public void removeInterestedCategory(Long categoryId, CustomUserDetails authUser) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));

        InterestedCategory interestedCategory = interestedCategoryRepository.findByUserAndCategory(user, category)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_INTERESTED_CATEGORY));

        interestedCategoryRepository.delete(interestedCategory);
    }

    /**
     * 특정 카테고리에 관심이 있는 사용자 조회 로직
     * @param categoryId 카테고리 ID
     * @return 관심이 있는 사용자 목록
     */
    public List<User> findUsersByInterestedCategory(Long categoryId) {
        return interestedCategoryRepository.findUsersByCategoryId(categoryId);
    }
}
