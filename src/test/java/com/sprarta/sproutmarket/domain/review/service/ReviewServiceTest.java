//package com.sprarta.sproutmarket.domain.review.service;
//
//
//import com.sprarta.sproutmarket.domain.item.entity.Item;
//import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
//import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
//import com.sprarta.sproutmarket.domain.review.entity.Review;
//import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
//import com.sprarta.sproutmarket.domain.review.repository.ReviewRepository;
//import com.sprarta.sproutmarket.domain.review.service.ReviewService;
//import com.sprarta.sproutmarket.domain.trade.entity.Trade;
//import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
//import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
//import com.sprarta.sproutmarket.domain.user.entity.User;
//import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ReviewServiceTest {
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Mock
//    private TradeRepository tradeRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Test
//    void 리뷰_생성_정상동작() {
//        // given
//        Long tradeId = 1L;
//
//        // 판매자와 구매자 Mock 설정
//        User seller = Mockito.mock(User.class);
//        User buyer = Mockito.mock(User.class);
//        Mockito.when(buyer.getId()).thenReturn(2L); // buyer의 ID 설정
//
//        // 거래 정보 설정
//        Trade trade = Mockito.mock(Trade.class);
//        Mockito.when(trade.getId()).thenReturn(tradeId);
//        Mockito.when(trade.getBuyer()).thenReturn(buyer);
//        Mockito.when(trade.getSeller()).thenReturn(seller);
//
//        // 현재 사용자 정보 설정
//        CustomUserDetails customUserDetails = new CustomUserDetails(buyer);
//        ReviewRequestDto dto = new ReviewRequestDto("친절함", ReviewRating.GOOD);
//
//        // tradeRepository에서 trade 반환 설정
//        when(tradeRepository.findById(tradeId)).thenReturn(Optional.of(trade));
//
//        // when
//        ReviewResponseDto response = reviewService.createReview(tradeId, dto, customUserDetails);
//
//        // then
//        assertNotNull(response);
//        assertEquals(tradeId, response.getTradeId());
//        assertEquals("친절함", response.getComment());
//        assertEquals(ReviewRating.GOOD, response.getReviewRating());
//
//        // 검증: repository 메서드 호출 및 plusRate 메서드 호출 여부 확인
//        verify(tradeRepository).findById(tradeId);
//        verify(reviewRepository).save(any(Review.class));
//        verify(seller).plusRate(); // plusRate 호출 검증
//        verify(seller, never()).minusRate();
//    }
//
//    @Test
//    void 리뷰_조회_정상동작() {
//        // given
//        Long tradeId = 1L;
//        Long reviewId = 1L;
//
//        Trade trade = Mockito.mock(Trade.class);
//        Mockito.when(trade.getId()).thenReturn(tradeId);
//
//        Review review = Mockito.mock(Review.class);
//        Mockito.when(review.getId()).thenReturn(reviewId);
//        Mockito.when(review.getTrade()).thenReturn(trade);
//        Mockito.when(review.getComment()).thenReturn("친절함");
//        Mockito.when(review.getReviewRating()).thenReturn(ReviewRating.GOOD);
//
//        when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));
//
//        // when
//        ReviewResponseDto response = reviewService.getReview(reviewId);
//
//        // then
//        assertNotNull(response);
//        assertEquals(1L, response.getId());
//        assertEquals(1L, response.getTradeId());
//        assertEquals("친절함", response.getComment());
//        assertEquals(ReviewRating.GOOD, response.getReviewRating());
//
//    }
//
//
//
//}
