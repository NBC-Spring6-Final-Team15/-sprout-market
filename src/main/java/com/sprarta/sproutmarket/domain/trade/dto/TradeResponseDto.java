package com.sprarta.sproutmarket.domain.trade.dto;


import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeResponseDto {

    private Long id;
    private Long itemId;
    private String sellerName;
    private String buyerName;
    private TradeStatus tradeStatus;

}
