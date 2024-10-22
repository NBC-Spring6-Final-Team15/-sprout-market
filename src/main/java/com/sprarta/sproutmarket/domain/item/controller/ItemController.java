package com.sprarta.sproutmarket.domain.item.controller;

import com.sprarta.sproutmarket.domain.common.dto.response.StatusResponse;
import com.sprarta.sproutmarket.domain.item.eto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/create")
    public ResponseEntity<StatusResponse> createItem(@RequestBody ItemCreateRequest itemCreateRequest){
        StatusResponse statusResponse = itemService.createItem(itemCreateRequest);
        return ResponseEntity.ok(statusResponse);
    }

}
