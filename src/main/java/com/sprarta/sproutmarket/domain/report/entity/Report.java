package com.sprarta.sproutmarket.domain.report.entity;


import com.sprarta.sproutmarket.domain.report.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reporting_reason;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Items items;


}
