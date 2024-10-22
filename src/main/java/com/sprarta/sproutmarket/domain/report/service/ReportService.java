package com.sprarta.sproutmarket.domain.report.service;


import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.report.dto.ReportRequestDto;
import com.sprarta.sproutmarket.domain.report.dto.ReportResponseDto;
import com.sprarta.sproutmarket.domain.report.entity.Report;
import com.sprarta.sproutmarket.domain.report.repository.ReportRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ItemRepository itemRepository;

    public ReportResponseDto createReport(Long itemId, ReportRequestDto dto, CustomUserDetails customUserDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NullPointerException("아이템이 존재하지 않습니다."));
        User user = User.fromAuthUser(customUserDetails);

        Report report = new Report(
                dto.getReportingReason(),
                user,
                item
        );
        Report savedReport = reportRepository.save(report);

        return new ReportResponseDto(
                savedReport.getId(),
                savedReport.getItem().getId(),
                savedReport.getReportingReason(),
                savedReport.getReportStatus()
        );

    }

    public ReportResponseDto getReport(Long reportId, CustomUserDetails customUserDetails) {
        Report report = reportRepository.findById(reportId).orElseThrow(() ->
                new NullPointerException("신고가 존재하지 않습니다."));

        return new ReportResponseDto(
                report.getId(),
                report.getItem().getId(),
                report.getReportingReason(),
                report.getReportStatus()
        );
    }



}
