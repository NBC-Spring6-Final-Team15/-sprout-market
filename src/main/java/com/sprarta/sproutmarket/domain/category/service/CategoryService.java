package com.sprarta.sproutmarket.domain.category.service;

import com.sprarta.sproutmarket.domain.category.dto.CategoryRequestDto;
import com.sprarta.sproutmarket.domain.category.dto.CategoryResponseDto;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category findByIdOrElseThrow(Long id){
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));
    }

    /**
     * 카테고리 추가 메서드
     * @param requestDto : 카테고리 이름을 담은 요청 DTO
     * @return 카테고리 ID, 카테고리 이름을 담아서 반환하는 응답 DTO
     */
    @Transactional
    public CategoryResponseDto create(CategoryRequestDto requestDto) {
        //추가하려는 카테고리가 이미 존재하는지 확인
        if(categoryRepository.existsByName(requestDto.getCategoryName())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_ALREADY_EXISTS_CATETORY);
        }

        Category category = categoryRepository.save(
                new Category(requestDto.getCategoryName()));

        return new CategoryResponseDto(category.getId(),category.getName());
    }

    //카테고리 전체 조회
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getActiveCategories() {
        List<Category> categories = categoryRepository.findAllByActiveStatus(Status.ACTIVE);

        return categories.stream().map(CategoryResponseDto::new).toList();
    }

    //카테고리 수정
    @Transactional
    public CategoryResponseDto update(Long categoryId, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));

        //수정될 이름과 현재 이름이 이미 같은지 확인
        if(category.getName().equals(requestDto.getCategoryName())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_SAME_NAME);
        }

        category.update(requestDto.getCategoryName());
        return new CategoryResponseDto(category.getId(),category.getName());
    }

    /**
     * 카테고리 논리삭제
     * @param categoryId 삭제처리할 카테고리 ID
     * @return : 삭제된 카테고리 ID, 이름을 담은 String
     */
    @Transactional
    public void delete(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));

        //이미 삭제된 카테고리인지 검증
        if(category.getStatus().equals(Status.DELETED)) {
            throw new ApiException(ErrorStatus.NOT_FOUND_CATEGORY);
        }

        category.deactivate();
    }

    //카테고리 복원

    //삭제된 카테고리만 조회

}
