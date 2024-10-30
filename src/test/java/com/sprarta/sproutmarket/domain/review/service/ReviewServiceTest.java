package com.sprarta.sproutmarket.domain.review.service;


import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
import com.sprarta.sproutmarket.domain.review.entity.Review;
import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import com.sprarta.sproutmarket.domain.review.repository.ReviewRepository;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private TradeRepository tradeRepository;

    @Spy
    private Item item;

    @Spy
    private User seller;

    private ChatRoom chatRoom;
    private CustomUserDetails buyerUserDetails;
    private Trade trade;
    @BeforeEach
    void setUp() {

        User buyer = new User("buyer","a@a","Qwerasdf1234!","buyer","01012345676","지구", UserRole.USER);
        ReflectionTestUtils.setField(buyer, "id", 1L);

        ReflectionTestUtils.setField(seller, "id", 2L);
        ReflectionTestUtils.setField(seller, "rate", 0);

        ReflectionTestUtils.setField(item, "id", 1L);
        ReflectionTestUtils.setField(item, "title", "아이템");
        ReflectionTestUtils.setField(item, "itemSaleStatus", ItemSaleStatus.WAITING);

        buyerUserDetails = new CustomUserDetails(buyer);

        chatRoom = new ChatRoom(buyer, seller, item);
        ReflectionTestUtils.setField(chatRoom, "id", 1L);

        trade = new Trade(chatRoom);
        ReflectionTestUtils.setField(trade, "id", 1L);
    }

    @Test
    void 리뷰_생성_정상동작() {
        ReviewRequestDto dto = new ReviewRequestDto("친절함", ReviewRating.GOOD);

        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));

        // when
        ReviewResponseDto response = reviewService.createReview(1L, dto, buyerUserDetails);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getTradeId());
        assertEquals("친절함", response.getComment());
        assertEquals(ReviewRating.GOOD, response.getReviewRating());
        assertEquals(1, seller.getRate());

        // 검증: repository 메서드 호출 및 plusRate 메서드 호출 여부 확인
        verify(tradeRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void 리뷰_조회_정상동작() {
        Review review = new Review("친절함", ReviewRating.GOOD, seller, trade);
        ReflectionTestUtils.setField(review, "id", 1L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // when
        ReviewResponseDto response = reviewService.getReview(1L);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getTradeId());
        assertEquals("친절함", response.getComment());
        assertEquals(ReviewRating.GOOD, response.getReviewRating());
    }
}
