package com.sprarta.sproutmarket.domain.areas.service;

import com.sprarta.sproutmarket.domain.areas.entity.AdministrativeArea;
import com.sprarta.sproutmarket.domain.areas.repository.AdministrativeAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmCachingService {
    private final AdministrativeAreaService administrativeAreaService;
    private final AdministrativeAreaRepository administrativeAreaRepository;

    public void cachingAllAdms() {
        List<AdministrativeArea> areas = administrativeAreaRepository.findAll();

        for (AdministrativeArea area : areas) {
            administrativeAreaService.getAdmNameListByAdmName(area.getAdmNm());
        }
    }
}
