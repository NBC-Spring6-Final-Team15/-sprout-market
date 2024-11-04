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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InterestedCategoryServiceTest {

    @InjectMocks
    private InterestedCategoryService interestedCategoryService;

    @Mock
    private InterestedCategoryRepository interestedCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addInterestedCategory_성공() {
        // given
        User user = mock(User.class);
        Category category = mock(Category.class);
        CustomUserDetails authUser = new CustomUserDetails(user);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(interestedCategoryRepository.existsByUserAndCategory(user, category)).willReturn(false);

        // when
        interestedCategoryService.addInterestedCategory(1L, authUser);

        // then
        verify(interestedCategoryRepository).save(any(InterestedCategory.class));
    }

    @Test
    void addInterestedCategory_유저없음() {
        // given
        CustomUserDetails authUser = new CustomUserDetails(mock(User.class));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            interestedCategoryService.addInterestedCategory(1L, authUser);
        });

        assertEquals(ErrorStatus.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    void removeInterestedCategory_성공() {
        // given
        User user = mock(User.class);
        Category category = mock(Category.class);
        InterestedCategory interestedCategory = mock(InterestedCategory.class);
        CustomUserDetails authUser = new CustomUserDetails(user);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(interestedCategoryRepository.findByUserAndCategory(user, category)).willReturn(Optional.of(interestedCategory));

        // when
        interestedCategoryService.removeInterestedCategory(1L, authUser);

        // then
        verify(interestedCategoryRepository).delete(interestedCategory);
    }

    @Test
    void findUsersByInterestedCategory_성공() {
        // given
        List<User> users = List.of(mock(User.class));
        given(interestedCategoryRepository.findUsersByCategoryId(anyLong())).willReturn(users);

        // when
        List<User> result = interestedCategoryService.findUsersByInterestedCategory(1L);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(interestedCategoryRepository).findUsersByCategoryId(anyLong());
    }
}