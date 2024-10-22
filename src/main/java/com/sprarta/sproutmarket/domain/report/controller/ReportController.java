package com.sprarta.sproutmarket.domain.report.controller;


import com.sprarta.sproutmarket.domain.report.dto.ReportRequestDto;
import com.sprarta.sproutmarket.domain.report.dto.ReportResponseDto;
import com.sprarta.sproutmarket.domain.report.service.ReportService;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReportController {

    private final ReportService reportService;

    // 생성
    @PostMapping("/reports/{itemId}")
    public ResponseEntity<ReportResponseDto> createReport(
            @PathVariable Long itemId,
            @RequestBody ReportRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ReportResponseDto responseDto = reportService.createReport(itemId, dto, customUserDetails);
        return ResponseEntity.ok(responseDto);
    }

    // 단건 조회
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ReportResponseDto> getReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ReportResponseDto responseDto = reportService.getReport(reportId, customUserDetails);
        return ResponseEntity.ok(responseDto);
    }

}
