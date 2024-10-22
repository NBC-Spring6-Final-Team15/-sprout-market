package com.sprarta.sproutmarket.domain.report.entity;


import com.sprarta.sproutmarket.domain.report.enums.ReportStatus;
import com.sprarta.sproutmarket.domain.user.entity.User;
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
    private String reportingReason;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;


}
