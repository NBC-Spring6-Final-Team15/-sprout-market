package com.sprarta.sproutmarket.domain.category.service;

import com.sprarta.sproutmarket.domain.category.dto.CategoryRequestDto;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.category.repository.CategoryRepository;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        given(categoryRepository.findByIdAndStatusIsActive(1L)).willReturn(Optional.of(category));

        categoryService.update(1L,requestDto);

        verify(categoryRepository,times(1)).findByIdAndStatusIsActive(1L);
        assertEquals(requestDto.getCategoryName(),category.getName());
    }

    @Test
    void 카테고리_삭제_성공() {
        given(categoryRepository.findByIdAndStatusIsActive(1L)).willReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).findByIdAndStatusIsActive(1L);
        assertEquals(Status.DELETED, category.getStatus());
    }

    @Test
    void findByIdOrElseThrow_whenCategoryExists_returnsCategory() {
        Long categoryId = 1L;
        Category category = new Category("Test Category");
        ReflectionTestUtils.setField(category,"id", 1L);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryService.findByIdOrElseThrow(categoryId);

        assertThat(result).isEqualTo(category);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void findByIdAndStatusIsActiveOrElseThrow_whenCategoryIsActive_returnsCategory() {
        Long categoryId = 1L;
        Category category = new Category("Active Category");
        // 카테고리 상태를 활성화합니다.
        category.activate();
        ReflectionTestUtils.setField(category, "id", categoryId);

        // Mocking repository method: categoryId로 활성화된 카테고리를 찾는 경우
        when(categoryRepository.findByIdAndStatusIsActive(categoryId)).thenReturn(Optional.of(category));

        // 메서드 호출
        Category result = categoryRepository.findByIdAndStatusIsActiveOrElseThrow(categoryId);

        // 결과 확인
        assertThat(result).isEqualTo(category);
        verify(categoryRepository, times(1)).findByIdAndStatusIsActive(categoryId);
    }

}