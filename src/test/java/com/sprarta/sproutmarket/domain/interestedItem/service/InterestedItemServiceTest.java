package com.sprarta.sproutmarket.domain.interestedItem.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem;
import com.sprarta.sproutmarket.domain.interestedItem.repository.InterestedItemRepository;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InterestedItemServiceTest {

    @InjectMocks
    private InterestedItemService interestedItemService;

    @Mock
    private InterestedItemRepository interestedItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addInterestedItem_성공() {
        // given
        User user = mock(User.class);
        Item item = mock(Item.class);
        CustomUserDetails authUser = new CustomUserDetails(user);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));

        // when
        interestedItemService.addInterestedItem(1L, authUser);

        // then
        verify(userRepository).save(user);
        verify(user).addInterestedItem(item);
    }

    @Test
    void addInterestedItem_유저_찾지_못함() {
        // given
        CustomUserDetails authUser = new CustomUserDetails(mock(User.class));

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            interestedItemService.addInterestedItem(1L, authUser);
        });

        assertEquals(ErrorStatus.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    void removeInterestedItem_성공() {
        // given
        User user = mock(User.class);
        Item item = mock(Item.class);
        CustomUserDetails authUser = new CustomUserDetails(user);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));

        // when
        interestedItemService.removeInterestedItem(1L, authUser);

        // then
        verify(userRepository).save(user);
        verify(user).removeInterestedItem(item);
    }

    @Test
    void getInterestedItems_성공() {
        // given
        User user = mock(User.class);
        CustomUserDetails authUser = new CustomUserDetails(user);

        Pageable pageable = PageRequest.of(0, 10);
        InterestedItem interestedItem = mock(InterestedItem.class);
        Item item = mock(Item.class);
        Category category = mock(Category.class);

        // Mocking the behavior
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(interestedItemRepository.findByUser(user, pageable)).willReturn(new PageImpl<>(Collections.singletonList(interestedItem)));
        given(interestedItem.getItem()).willReturn(item);
        given(item.getId()).willReturn(1L);
        given(item.getTitle()).willReturn("Item Title");
        given(item.getDescription()).willReturn("Item Description");
        given(item.getPrice()).willReturn(10000);
        given(item.getSeller()).willReturn(user);
        given(user.getNickname()).willReturn("Seller Nickname");

        // Ensure category is not null and has a name
        given(item.getCategory()).willReturn(category);
        given(category.getName()).willReturn("Category Name");

        // when
        Page<ItemResponseDto> result = interestedItemService.getInterestedItems(authUser, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Item Title", result.getContent().get(0).getTitle());
        assertEquals("Category Name", result.getContent().get(0).getCategoryName());
        verify(interestedItemRepository).findByUser(user, pageable);
    }

    @Test
    void findUsersByInterestedItem_성공() {
        // given
        InterestedItem interestedItem = mock(InterestedItem.class);
        User user = mock(User.class);
        given(interestedItem.getUser()).willReturn(user);
        given(interestedItemRepository.findByItemId(anyLong())).willReturn(Collections.singletonList(interestedItem));

        // when
        List<User> users = interestedItemService.findUsersByInterestedItem(1L);

        // then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
        verify(interestedItemRepository).findByItemId(anyLong());
    }
}
