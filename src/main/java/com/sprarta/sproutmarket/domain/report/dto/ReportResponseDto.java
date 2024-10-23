package com.sprarta.sproutmarket.domain.report.dto;


import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.report.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportResponseDto {

    private Long id;
    private Long itemId;
    private String reportingReason;
    private ReportStatus reportStatus;

}
