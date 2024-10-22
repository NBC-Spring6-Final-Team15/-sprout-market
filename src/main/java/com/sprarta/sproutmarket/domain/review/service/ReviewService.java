package com.sprarta.sproutmarket.domain.review.service;


import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
import com.sprarta.sproutmarket.domain.review.entity.Review;
import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import com.sprarta.sproutmarket.domain.review.repository.ReviewRepository;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponseDto createReview(Long tradeId, ReviewRequestDto dto, CustomUserDetails customUserDetails) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_TRADE));

        User user = User.fromAuthUser(customUserDetails);
        if (dto.getReviewRating()==ReviewRating.GOOD) {
            user.plusRate();
        }
        if (dto.getReviewRating()==ReviewRating.BAD) {
            user.minusRate();
        }
        userRepository.save(user);

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

    public ReviewResponseDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_REVIEW));

        return new ReviewResponseDto(
                review.getId(),
                review.getTrade().getId(),
                review.getComment(),
                review.getReviewRating()
        );
    }


    public List<ReviewResponseDto> getReviews(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_USER));

        List<Review> reviews = reviewRepository.findByUserId(user.getId());

        List<ReviewResponseDto> responseDtos = new ArrayList<>();

        for (Review review : reviews) {
            ReviewResponseDto responseDto = new ReviewResponseDto(
                    review.getId(),
                    review.getTrade().getId(),
                    review.getComment(),
                    review.getReviewRating()
                    );
            responseDtos.add(responseDto);
        }
        return responseDtos;

    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto, CustomUserDetails customUserDetails) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(customUserDetails.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_REVIEW_UPDATE);
        }

        review.update(
                dto.getComment(),
                dto.getReviewRating()
        );
        reviewRepository.save(review);
        return new ReviewResponseDto(
                review.getId(),
                review.getTrade().getId(),
                review.getComment(),
                review.getReviewRating()
        );

    }

    @Transactional
    public void deleteReview(Long reviewId, CustomUserDetails customUserDetails) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(customUserDetails.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_REVIEW_DELETE);
        }

        reviewRepository.delete(review);
    }

}
