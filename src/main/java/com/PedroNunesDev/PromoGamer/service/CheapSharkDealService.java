package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.CheapSharkDealDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheapSharkDealService {

    private final CheapSharkApiService cheapSharkApiService;
    @Value("${spring.address.email}")
    private String addressEmail;

    public List<CheapSharkDealDTO> getDealsByStore(Long storeId, Long pageNumber){

        log.info("Searching Deals in store with id: [{}] and page number: [{}]", storeId,pageNumber);

        List<CheapSharkDealDTO> deals = cheapSharkApiService.getDealsByStoreId("PromoGamer/1.0 ("+addressEmail+")",storeId,pageNumber);

        log.info("Searched completed, with size: [{}]", deals.size());

        return deals;
    }
}
