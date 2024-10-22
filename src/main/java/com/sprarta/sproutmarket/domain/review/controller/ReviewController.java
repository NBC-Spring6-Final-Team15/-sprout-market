package com.sprarta.sproutmarket.domain.review.controller;


import com.sprarta.sproutmarket.domain.report.dto.ReportResponseDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
import com.sprarta.sproutmarket.domain.review.service.ReviewService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    // 생성
    @PostMapping("/reviews/{tradeId}")
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable Long tradeId,
            @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        ReviewResponseDto responseDto = reviewService.createReview(tradeId, dto, customUserDetails);
        return ResponseEntity.ok(responseDto);
    }

    // 단건 조회
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ReviewResponseDto responseDto = reviewService.getReview(reviewId, customUserDetails);
        return ResponseEntity.ok(responseDto);
    }



}


