package com.sprarta.sproutmarket.domain.trade.dto;


import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeResponseDto {
    private Long id;
    private String itemTitle;
    private String sellerName;
    private String buyerName;
    private TradeStatus tradeStatus;
    private Long buyerId;
    private Long sellerId;

    private TradeResponseDto(Long id, String itemTitle, String sellerName, String buyerName, TradeStatus tradeStatus, Long buyerId, Long sellerId) {
        this.id = id;
        this.itemTitle = itemTitle;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.tradeStatus = tradeStatus;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
    }

    public static TradeResponseDto from(Trade trade) {
        return new TradeResponseDto(
                trade.getId(),
                trade.getChatRoom().getItem().getTitle(),
                trade.getChatRoom().getSeller().getNickname(),
                trade.getChatRoom().getBuyer().getNickname(),
                trade.getTradeStatus(),
                trade.getChatRoom().getBuyer().getId(),
                trade.getChatRoom().getSeller().getId()
        );
    }
}
