package com.sprarta.sproutmarket.domain.trade.dto;


import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TradeRequestDto {

    @NotNull
    private Long buyerId;

}
