package com.sprarta.sproutmarket.domain.category.service;

import com.sprarta.sproutmarket.domain.category.dto.CategoryRequestDto;
import com.sprarta.sproutmarket.domain.category.dto.CategoryResponseDto;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category findByIdOrElseThrow(Long id){
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));
    }

    /**
     * 카테고리 추가 메서드
     * @param requestDto : 카테고리 이름을 담은 요청 DTO
     * @return 카테고리 ID, 카테고리 이름을 담아서 반환하는 응답 DTO
     */
    public CategoryResponseDto create(CategoryRequestDto requestDto) {
        //추가하려는 카테고리가 이미 존재하는지 확인
        if(categoryRepository.existsByName(requestDto.getCategoryName())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_ALREADY_EXISTS_CATETORY);
        }
        Category newCategory = new Category(requestDto.getCategoryName());
        Category savedCategory = categoryRepository.save(newCategory);

        return new CategoryResponseDto(savedCategory.getId(),savedCategory.getName());
    }

    //카테고리 전체 조회
    public List<CategoryResponseDto> findAll() {
        List<Category> allCategories = categoryRepository.findAll();
        return allCategories.stream().map(CategoryResponseDto::new).toList();
    }

    //카테고리 수정

    //카테고리 삭제

}
