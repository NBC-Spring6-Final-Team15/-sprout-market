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

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category findByIdOrElseThrow(Long id){
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));
    }

    //카테고리 생성 어드민만 가능
    @Transactional
    public CategoryResponseDto create(CategoryRequestDto requestDto) {
        //추가하려는 카테고리가 이미 존재하는지 확인
        if(categoryRepository.existsByName(requestDto.getCategoryName())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_ALREADY_EXISTS_CATETORY);
        }
        Category newCategory = new Category(requestDto.getCategoryName());
        Category savedCategory = categoryRepository.save(newCategory);

        return new CategoryResponseDto(savedCategory.getId(),savedCategory.getName());
    }

}
