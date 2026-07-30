package com.PedroNunesDev.PromoGamer.controller;

import com.PedroNunesDev.PromoGamer.dto.DealDtoResponse;
import com.PedroNunesDev.PromoGamer.service.DealService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deals")
@Tag(name = "Deal Controller", description = "Responsible for actions related to Deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping
    public ResponseEntity<List<DealDtoResponse>> registerDeals(@RequestParam Long storeId, @RequestParam Long pageNumber){


        List<DealDtoResponse> deals = dealService.registerDeals(storeId,pageNumber);

        return ResponseEntity.ok(deals);
    }
}
