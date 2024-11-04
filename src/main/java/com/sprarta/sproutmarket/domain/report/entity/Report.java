package com.sprarta.sproutmarket.domain.report.entity;


import com.sprarta.sproutmarket.domain.common.Timestamped;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.report.enums.ReportStatus;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportingReason;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus = ReportStatus.WAITING;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;


    public Report(String reportingReason, User user, Item item) {
        this.reportingReason = reportingReason;
        this.user = user;
        this.item = item;
    }

    public void update(String reportingReason) {
        this.reportingReason = reportingReason;
    }
}
