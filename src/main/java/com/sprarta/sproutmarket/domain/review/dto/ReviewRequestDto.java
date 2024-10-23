package com.sprarta.sproutmarket.domain.review.dto;


import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

    @NotNull
    private String comment;

    @NotNull
    private ReviewRating reviewRating;

}
