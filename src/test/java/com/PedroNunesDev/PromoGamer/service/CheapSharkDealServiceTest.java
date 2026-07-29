package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.CheapSharkDealDTO;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CheapSharkDealServiceTest {

    @Mock
    private CheapSharkApiService cheapSharkApiService;

    @InjectMocks
    private CheapSharkDealService cheapSharkDealService;

    @BeforeEach
    void setupValue(){
        ReflectionTestUtils.setField(
                cheapSharkDealService,
                "addressEmail",
                "teste@teste.com"
        );
    }

    @Test
    void shouldReturnDealsWhenApiReturnsDeals() {

        // Arrange

        CheapSharkDealDTO deal = new CheapSharkDealDTO(
                "ELDENRING",
                "ELDEN RING",
                "12345",
                "1",
                "999",
                "149.99",
                "249.99",
                "1",
                "40.00",
                "96",
                "Overwhelmingly Positive",
                "97",
                "500000",
                "1245620",
                1642118400L,
                1710000000L,
                "9.8",
                "https://cdn.cheapshark.com/thumb.jpg"
        );

        List<CheapSharkDealDTO> expectedDeals = List.of(deal);

        when(cheapSharkApiService.getDealsByStoreId(
                "PromoGamer/1.0 (teste@teste.com)",
                1L,
                0L
        )).thenReturn(expectedDeals);

        // Act

        List<CheapSharkDealDTO> result =
                cheapSharkDealService.getDealsByStore(1L, 0L);

        // Assert

        assertNotNull(result);
        assertEquals(1, result.size());

        CheapSharkDealDTO returnedDeal = result.getFirst();

        assertEquals("ELDEN RING", returnedDeal.title());
        assertEquals("149.99", returnedDeal.salePrice());
        assertEquals("249.99", returnedDeal.normalPrice());
        assertEquals("1", returnedDeal.storeID());

        verify(cheapSharkApiService).getDealsByStoreId(
                "PromoGamer/1.0 (teste@teste.com)",
                1L,
                0L
        );
    }

    @Test
    void shouldReturnEmptyListWhenApiReturnsEmptyList(){

        // Arrange

        when(cheapSharkApiService.getDealsByStoreId(
                "PromoGamer/1.0 (teste@teste.com)",
                1L,
                0L
        )).thenReturn(Collections.emptyList());

        // Act

        List<CheapSharkDealDTO> result = cheapSharkDealService.getDealsByStore(
                1L,
                0L
        );

        // Assert

        assertTrue(result.isEmpty());

        verify(cheapSharkApiService)
                .getDealsByStoreId(
                        "PromoGamer/1.0 (teste@teste.com)",
                        1L,
                        0L
                );
    }
}
