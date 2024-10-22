package com.sprarta.sproutmarket.domain.review.service;


import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
import com.sprarta.sproutmarket.domain.review.entity.Review;
import com.sprarta.sproutmarket.domain.review.repository.ReviewRepository;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TradeRepository tradeRepository;

    public ReviewResponseDto createReview(Long tradeId, ReviewRequestDto dto, CustomUserDetails customUserDetails) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() ->
                new NullPointerException("리뷰가 존재하지 않습니다."));
        User user = User.fromAuthUser(customUserDetails);

        Review review = new Review(
                dto.getComment(),
                dto.getReviewRating(),
                user,
                trade
        );
        reviewRepository.save(review);

        return new ReviewResponseDto(
                review.getId(),
                review.getTrade().getId(),
                review.getComment(),
                review.getReviewRating()
        );

    }

    public ReviewResponseDto getReview(Long reviewId, CustomUserDetails customUserDetails) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new NullPointerException("리뷰가 존재하지 않습니다."));

        return new ReviewResponseDto(
                review.getId(),
                review.getTrade().getId(),
                review.getComment(),
                review.getReviewRating()
        );
    }



}
