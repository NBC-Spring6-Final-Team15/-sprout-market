package com.sprarta.sproutmarket.domain.review.entity;


import com.sprarta.sproutmarket.domain.common.Timestamped;
import com.sprarta.sproutmarket.domain.report.enums.ReportStatus;
import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    private ReviewRating reviewRating;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToOne
    @JoinColumn(name = "trade_id")
    private Trade trade;


    public Review(String comment, ReviewRating reviewRating, User seller, Trade trade) {
        this.comment = comment;
        this.reviewRating = reviewRating;
        this.seller = seller;
        this.trade = trade;
    }

    public void update(String comment, ReviewRating reviewRating) {
        if (comment != null) {
            this.comment = comment;
        }
        if (reviewRating != null) {
            this.reviewRating = reviewRating;
        }

    }
}
