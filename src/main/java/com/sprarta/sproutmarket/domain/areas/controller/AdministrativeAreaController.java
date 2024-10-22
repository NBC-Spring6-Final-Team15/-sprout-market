package com.sprarta.sproutmarket.domain.areas.controller;

import com.sprarta.sproutmarket.domain.areas.dto.AdministrativeAreaRequestDto;
import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AdministrativeAreaController {
    private final AdministrativeAreaService administrativeAreaService;

    @GetMapping("/test/api")
    public void addgeoJson() throws IOException {
        administrativeAreaService.insertGeoJsonData("json/HangJeongDong.geojson");
    }

    @GetMapping("/test/api/getHMD")
    public String getHMD(@RequestBody AdministrativeAreaRequestDto requestDto) {
        return administrativeAreaService.findAdministrativeAreaByCoordinates(requestDto.getLongitude(), requestDto.getLatitude());
    }
}
