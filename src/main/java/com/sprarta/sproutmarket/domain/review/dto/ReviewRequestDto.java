package com.sprarta.sproutmarket.domain.review.dto;


import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

    private String comment;
    private ReviewRating reviewRating;

}
