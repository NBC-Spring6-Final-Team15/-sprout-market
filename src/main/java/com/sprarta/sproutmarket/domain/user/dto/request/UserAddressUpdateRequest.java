package com.sprarta.sproutmarket.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressUpdateRequest {
    private double longitude;
    private double latitude;
}
