package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.CheapSharkDealDTO;
import com.PedroNunesDev.PromoGamer.dto.DealDtoResponse;
import com.PedroNunesDev.PromoGamer.enums.DealEnumStatus;
import com.PedroNunesDev.PromoGamer.model.Deal;
import com.PedroNunesDev.PromoGamer.repository.DealRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class DealServiceTest {

    @Mock
    private CheapSharkDealService cheapSharkDealService;
    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private DealService dealService;

    @Test
    void shouldSaveNewDealsFoundOnFirstPage() {

        // Arrange
        Long storeId = 1L;

        //DTO que será retornado quando chamar o metodo de buscar deals
        CheapSharkDealDTO dto = new CheapSharkDealDTO(
                "Cool Game",
                "dealId123",
                "1234",
                "90",
                1234L
        );

        //Mostra pro Mockito que deve retornar uma lista com o DTO acima
        when(cheapSharkDealService.getDealsByStore(storeId, 0L))
                .thenReturn(List.of(dto));

        //Quando chamar o metodo do repository retornar uma Collection vazia | nova deal
        when(dealRepository.getExistingDealsId(Set.of("dealId123")))
                .thenReturn(Set.of());

        //Cria uma nova Deal para ser retornada na lista quando chamar o saveAll do banco
        Deal dealEntity = Deal.builder()
                .dealId("dealId123")
                .steamAppId("1234")
                .steamRatingPercent("90")
                .title("Cool Game")
                .dealEnumStatus(DealEnumStatus.PENDENTE)
                .build();

        when(dealRepository.saveAll(anyList()))
                .thenReturn(List.of(dealEntity));

        // Act
        List<DealDtoResponse> result = dealService.registerDeals(storeId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); //Verifica se o tamanho de items no result é 1, como programamos
        assertEquals(dealEntity.getDealId(), result.getFirst().dealId());

        verify(dealRepository, times(1)).saveAll(anyList()); //Verifica que o saveAll foi chamado 1 vez
        verify(cheapSharkDealService, times(1)).getDealsByStore(storeId, 0L); // Verifica que o getDealsByStore foi chamado 1 vez
    }


    @Test
    void shouldSkipToNextPageWhenCurrentPageHasOnlyRepeatedDeals() {

        // Arrange
        Long storeId = 1L;

        CheapSharkDealDTO repeated = new CheapSharkDealDTO(
                "Cool Game", "old-deal", "12345", "90", 1234L
        );
        CheapSharkDealDTO newDeal = new CheapSharkDealDTO(
                "Cool Game 2", "new-deal", "12345", "90", 1234L
        );

        when(cheapSharkDealService.getDealsByStore(storeId, 0L))
                .thenReturn(List.of(repeated));
        when(dealRepository.getExistingDealsId(Set.of("old-deal")))
                .thenReturn(Set.of("old-deal"));

        when(cheapSharkDealService.getDealsByStore(storeId, 1L))
                .thenReturn(List.of(newDeal));
        when(dealRepository.getExistingDealsId(Set.of("new-deal")))
                .thenReturn(Set.of());

        Deal dealEntity = Deal.builder()
                .dealId("new-deal")
                .title("Cool Game")
                .dealEnumStatus(DealEnumStatus.PENDENTE)
                .steamAppId("12345")
                .steamRatingPercent("90")
                .build();

        when(dealRepository.saveAll(anyList()))
                .thenReturn(List.of(dealEntity));

        // Act
        List<DealDtoResponse> result = dealService.registerDeals(storeId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(dealEntity.getDealId(), result.getFirst().dealId());

        verify(cheapSharkDealService, times(1)).getDealsByStore(storeId, 0L);
        verify(cheapSharkDealService, times(1)).getDealsByStore(storeId, 1L);

        verify(dealRepository,times(1)).saveAll(anyList());
    }

    @Test
    void shouldReturnEmptyListAndNotSaveAfterFivePagesWithNoNewDeals() {

        // Arrange
        Long storeId = 1L;

        when(cheapSharkDealService.getDealsByStore(eq(storeId), anyLong()))
                .thenReturn(List.of());
        when(dealRepository.getExistingDealsId(anySet()))
                .thenReturn(Set.of());

        // Act
        List<DealDtoResponse> result = dealService.registerDeals(storeId);

        // Assert
        assertTrue(result.isEmpty());

        verify(dealRepository, never()).saveAll(anyList());
        verify(cheapSharkDealService, times(5)).getDealsByStore(eq(storeId), anyLong());
    }
}