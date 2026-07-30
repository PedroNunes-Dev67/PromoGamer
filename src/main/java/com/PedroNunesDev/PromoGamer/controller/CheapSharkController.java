package com.PedroNunesDev.PromoGamer.controller;

import com.PedroNunesDev.PromoGamer.dto.CheapSharkDealDTO;
import com.PedroNunesDev.PromoGamer.service.CheapSharkDealService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@Tag(name="CheapShark Controller", description = "Searching deals using API by CheapShark")
@RequiredArgsConstructor
public class CheapSharkController {

    private final CheapSharkDealService cheapSharkDealService;

    @GetMapping
    public ResponseEntity<List<CheapSharkDealDTO>> getDealsByStore(@RequestParam Long storeId, @RequestParam Long pageNumber){

        List<CheapSharkDealDTO> deals = cheapSharkDealService.getDealsByStore(storeId,pageNumber);

        return ResponseEntity.ok(deals);
    }
}
