package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.CheapSharkDealDTO;
import com.PedroNunesDev.PromoGamer.dto.DealDtoResponse;
import com.PedroNunesDev.PromoGamer.enums.DealEnumStatus;
import com.PedroNunesDev.PromoGamer.model.Deal;
import com.PedroNunesDev.PromoGamer.repository.DealRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DealService {

    private final CheapSharkDealService cheapSharkDealService;
    private final DealRepository dealRepository;

    public List<DealDtoResponse> registerDeals(Long storeId, Long pageNumber){

        // Busca as promoções via API do CheapShhark
        List<CheapSharkDealDTO> dealsSearched = cheapSharkDealService.getDealsByStore(storeId,pageNumber);

        // Pega apenas os dealId dessas promoções
        Set<String> dealsSearchedId = dealsSearched
                .stream()
                .map(CheapSharkDealDTO::dealID)
                .collect(Collectors.toSet());

        // Consulta no banco e pega apenas os dealId existentes no banco
        Set<String> existingDealsId = dealRepository.getExistingDealsId(dealsSearchedId);

        // Filtra a resposta da API, pegando apenas os que não tiverem o dealId já salvo
        List<CheapSharkDealDTO> newDeals = dealsSearched.stream()
                .filter(dto -> !existingDealsId.contains(dto.dealID()))
                .toList();

        if (newDeals.isEmpty()) {
            log.info("Nenhuma nova promoção encontrada...");
            return List.of();
        }

        // Cria uma lista de novas Deal para ser salva no banco
        List<Deal> dealsForSaving = newDeals.stream()
                .map(deal -> {
                    return Deal.builder()
                            .dealId(deal.dealID())
                            .title(deal.title())
                            .dealEnumStatus(DealEnumStatus.PENDENTE)
                            .steamAppId(deal.steamAppId())
                            .steamRatingPercent(deal.steamRatingPercent())
                            .build();
                })
                .toList();

        // Lista salva no banco
        List<Deal> dealsSaved = dealRepository.saveAll(dealsForSaving);

        // Retorna uma lista do DTO de Deal
        return dealsSaved
                .stream()
                .map(deal -> {
                    return new DealDtoResponse(
                      deal.getDealId(),
                      deal.getTitle(),
                      deal.getSteamAppId()
                    );
                }).toList();
    }
}
