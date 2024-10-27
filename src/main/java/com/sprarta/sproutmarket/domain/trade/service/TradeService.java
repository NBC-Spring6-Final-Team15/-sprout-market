package com.sprarta.sproutmarket.domain.trade.service;


import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.trade.dto.TradeRequestDto;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.trade.repository.TradeRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // 예약
    @Transactional
    public TradeResponseDto reserveTrade(Long itemId, TradeRequestDto dto, CustomUserDetails customUserDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_ITEM));

        if (item.getItemSaleStatus()==ItemSaleStatus.RESERVED) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_CONFLICT_TRADE_RESERVATION);
        }

        if (item.getItemSaleStatus()==ItemSaleStatus.SOLD) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_CONFLICT_TRADE);
        }

        item.changeSaleStatus(ItemSaleStatus.RESERVED);

        User seller =  userRepository.findById(customUserDetails.getId()).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_USER));

        User buyer = userRepository.findById(dto.getBuyerId()).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_USER));

        if (!seller.getId().equals(item.getSeller().getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_SELLER);
        }
        Trade trade = new Trade(seller, buyer, item, TradeStatus.RESERVED);
        tradeRepository.save(trade);

        return new TradeResponseDto(
                trade.getId(),
                item.getId(),
                seller.getNickname(),
                buyer.getNickname(),
                trade.getTradeStatus()
        );
    }

    // 예약 -> 판매완료로 변경
    @Transactional
    public TradeResponseDto finishTrade(Long itemId, TradeRequestDto dto, CustomUserDetails customUserDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_ITEM));
        item.changeSaleStatus(ItemSaleStatus.SOLD);

        User seller =  userRepository.findById(customUserDetails.getId()).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_USER));

        User buyer = userRepository.findById(dto.getBuyerId()).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_USER));

        if (!seller.getId().equals(item.getSeller().getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_SELLER);
        }
        Trade trade = tradeRepository.findByItemId(item.getId()).orElseThrow(() ->
                new ApiException(ErrorStatus.NOT_FOUND_TRADE));

        trade.updateTradeStatus(TradeStatus.COMPLETED);

        return new TradeResponseDto(
                trade.getId(),
                item.getId(),
                seller.getNickname(),
                buyer.getNickname(),
                trade.getTradeStatus()
        );

    }
}
