package com.sprarta.sproutmarket.domain.category.service;

import com.sprarta.sproutmarket.domain.category.dto.CategoryRequestDto;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("디지털");
        ReflectionTestUtils.setField(category,"id",1L);
        ReflectionTestUtils.setField(category,"status",Status.ACTIVE);
    }

    @Test
    void 카테고리_생성_성공() {
        CategoryRequestDto requestDto = new CategoryRequestDto("디지털");
        given(categoryRepository.existsByName(requestDto.getCategoryName())).willReturn(false);
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        categoryService.create(requestDto);

        verify(categoryRepository,times(1)).existsByName(requestDto.getCategoryName());
        verify( categoryRepository,times(1)).save(any(Category.class));
    }

    @Test
    void 카테고리_수정_성공() {
        CategoryRequestDto requestDto = new CategoryRequestDto("가구");

        given(categoryRepository.findByIdAndStatusIsActiveOrElseThrow(1L)).willReturn(category);

        categoryService.update(1L,requestDto);

        verify(categoryRepository,times(1)).findByIdAndStatusIsActiveOrElseThrow(1L);
        assertEquals(requestDto.getCategoryName(),category.getName());
    }

    @Test
    void 카테고리_삭제_성공() {
        given(categoryRepository.findByIdAndStatusIsActiveOrElseThrow(1L)).willReturn(category);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).findByIdAndStatusIsActiveOrElseThrow(1L);
        assertEquals(Status.DELETED, category.getStatus());
    }
}