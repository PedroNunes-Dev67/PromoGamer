package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.*;
import com.PedroNunesDev.PromoGamer.enums.DealEnumStatus;
import com.PedroNunesDev.PromoGamer.enums.DealSourceType;
import com.PedroNunesDev.PromoGamer.model.Deal;
import com.PedroNunesDev.PromoGamer.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class SteamServiceTest {

    @Mock
    private SteamApiService steamApiService;

    @Mock
    private MessageTemplateBuilder messageTemplateBuilder;

    @InjectMocks
    private SteamService steamService;

    @BeforeEach
    void setupValue(){
        ReflectionTestUtils.setField(
                steamService,
                "groupNumber",
                "teste"
        );
    }

    @Test
    void shouldReturnMessageForBaseGame(){

        // arrange

        Deal deal = createDeal();

        SteamAppDataDTO appDataDTO = createSteamAppDTO();

        when(steamApiService.getAppDetails("1234","br", "brazilian", "basic,price_overview"))
                .thenReturn(Map.of("1234", new SteamDetailsWrapper<>(true, appDataDTO)));

        when(messageTemplateBuilder.buildCaptionForApp(
                any(SteamAppDataDTO.class),anyString()))
                .thenReturn("caption");

        // act

        Optional<Message> result = steamService.buildMessageFromDeal(deal);
        Message message = result.get();

        // assert

        assertThat(result).isNotEmpty();
        assertThat(message.getDeal()).isEqualTo(deal);
        assertThat(message.getSendAt()).isNull();
        assertThat(message.getSourceType()).isEqualTo(DealSourceType.BASE_GAME);

        verify(steamApiService, times(1))
                .getAppDetails("1234","br", "brazilian", "basic,price_overview");
        verify(steamApiService, never())
                .getPackageDetails(anyString(), anyString(), anyString());
        verify(messageTemplateBuilder, times(1))
                .buildCaptionForApp(any(SteamAppDataDTO.class), anyString());
    }

    @Test
    void shouldReturnMessageForPackageGame(){

        // arrange

        Deal deal = createDeal();

        SteamPackageDataDTO packageDataDTO = createSteamPackageDTO();

        when(steamApiService.getAppDetails("1234","br", "brazilian", "basic,price_overview"))
                .thenReturn(Map.of("1234", new SteamDetailsWrapper<>(false, null)));

        when(steamApiService.getPackageDetails("1234","br", "brazilian"))
                .thenReturn(Map.of("1234", new SteamDetailsWrapper<>(true,packageDataDTO)));

        when(messageTemplateBuilder.buildCaptionForPackage(
                any(SteamPackageDataDTO.class),anyString()))
                .thenReturn("caption");

        // act

        Optional<Message> result = steamService.buildMessageFromDeal(deal);
        Message message = result.get();

        // assert

        assertThat(result).isNotEmpty();
        assertThat(message.getDeal()).isEqualTo(deal);
        assertThat(message.getSendAt()).isNull();
        assertThat(message.getSourceType()).isEqualTo(DealSourceType.PACKAGE);

        verify(steamApiService, times(1))
                .getAppDetails("1234","br", "brazilian", "basic,price_overview");
        verify(steamApiService, times(1))
                .getPackageDetails("1234","br", "brazilian");
        verify(messageTemplateBuilder, times(1))
                .buildCaptionForPackage(any(SteamPackageDataDTO.class), anyString());
    }

    @Test
    void shouldReturnEmptyWhenAppAndPackageAreInvalid(){

        // arrange

        Deal deal = createDeal();

        when(steamApiService.getAppDetails("1234","br", "brazilian", "basic,price_overview"))
                .thenReturn(Map.of("1234", new SteamDetailsWrapper<>(false, null)));

        when(steamApiService.getPackageDetails("1234","br", "brazilian"))
                .thenReturn(Map.of("1234", new SteamDetailsWrapper<>(false,null)));


        // act

        Optional<Message> result = steamService.buildMessageFromDeal(deal);

        // assert

        assertThat(result).isEmpty();

        verify(steamApiService)
                .getAppDetails("1234","br", "brazilian", "basic,price_overview");
        verify(steamApiService)
                .getPackageDetails("1234","br", "brazilian");
        verifyNoInteractions(messageTemplateBuilder);
    }

    @Test
    void shouldThrowIllegalExceptionWhenDealIsNull(){

        // act

        assertThatThrownBy(() -> steamService.buildMessageFromDeal(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Deal não pode ser null");

        verifyNoInteractions(steamApiService);
        verifyNoInteractions(messageTemplateBuilder);
    }

    @Test
    void shouldThrowIllegalExceptionWhenSteamAppIdIsNull(){

        // arrange

        Deal deal = createDeal();
        deal.setSteamAppId(null);

        // act

        assertThatThrownBy(() -> steamService.buildMessageFromDeal(deal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Steam app id não pode ser null");

        verifyNoInteractions(steamApiService);
        verifyNoInteractions(messageTemplateBuilder);
    }

    private Deal createDeal(){
        return Deal.builder()
                .dealId("1234")
                .title("Crazy game")
                .steamAppId("1234")
                .steamRatingPercent("9876")
                .dealEnumStatus(DealEnumStatus.PENDENTE)
                .creationDate(LocalDateTime.now())
                .build();
    }
    private SteamAppDataDTO createSteamAppDTO(){
        return new SteamAppDataDTO("Crazy game", "1234","https://", new SteamPriceOverviewDTO(
                "BRL",
                2599,
                1299,
                50,
                "R$ 25,99",
                "R$ 12,99"
        ));
    }
    private SteamPackageDataDTO createSteamPackageDTO(){
        return new SteamPackageDataDTO(
                "Crazy game",
                "https://",
                "https://",
                new SteamPackagePriceDTO(
                        "1000",
                        100,
                        50,
                        50,
                        50
                )
        );
    }
}