package com.sprarta.sproutmarket.domain.category.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category findByIdOrElseThrow(Long id){
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY));
    }
}
