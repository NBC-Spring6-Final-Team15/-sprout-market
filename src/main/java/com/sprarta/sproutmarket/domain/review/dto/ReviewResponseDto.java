package com.sprarta.sproutmarket.domain.review.dto;


import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private Long tradeId;
    private String comment;
    private ReviewRating reviewRating;

}
