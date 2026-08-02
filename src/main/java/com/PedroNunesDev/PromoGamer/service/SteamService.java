package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.SteamAppDataDTO;
import com.PedroNunesDev.PromoGamer.dto.SteamDetailsWrapper;
import com.PedroNunesDev.PromoGamer.dto.SteamPackageDataDTO;
import com.PedroNunesDev.PromoGamer.enums.DealSourceType;
import com.PedroNunesDev.PromoGamer.model.Deal;
import com.PedroNunesDev.PromoGamer.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamService {

    private final SteamApiService steamApiService;
    private final MessageTemplateBuilder templateBuilder;

    @Value("${promogamer.whatsapp.group-number}")
    private String groupNumber;

    public Optional<Message> buildMessageFromDeal(Deal deal) {

        Assert.notNull(deal, "Deal não pode ser null");
        Assert.notNull(deal.getSteamAppId(), "Steam app id não pode ser null");

        String steamAppId = deal.getSteamAppId();

        log.info("Iniciando busca de detalhes do jogo com steam app id: [{}]", steamAppId);

        Map<String, SteamDetailsWrapper<SteamAppDataDTO>> appResponse = steamApiService.getAppDetails(
                steamAppId, "br", "brazilian", "basic,price_overview"
        );

        SteamDetailsWrapper<SteamAppDataDTO> appWrapper = appResponse.get(steamAppId);

        if (appWrapper != null && appWrapper.success()) {
            Message message = buildMessageFromAppData(deal, appWrapper.data());
            return Optional.of(message);
        }

        log.info("App id [{}] não é um app válido, tentando como package...", steamAppId);

        Map<String, SteamDetailsWrapper<SteamPackageDataDTO>> packageResponse = steamApiService.getPackageDetails(
                steamAppId, "br", "brazilian"
        );

        SteamDetailsWrapper<SteamPackageDataDTO> packageWrapper = packageResponse.get(steamAppId);

        if (packageWrapper != null && packageWrapper.success()) {
            Message message = buildMessageFromPackageData(deal, packageWrapper.data());
            return Optional.of(message);
        }

        log.warn("Steam app id [{}] não é válido nem como app nem como package. Deal será ignorada.", steamAppId);
        return Optional.empty();
    }

    private Message buildMessageFromAppData(Deal deal, SteamAppDataDTO data) {

        String imageUrl = data.headerImage();
        String storeUrl = "https://store.steampowered.com/app/" + data.steamAppId();

        String caption = templateBuilder.buildCaptionForApp(data, storeUrl);

        return Message.builder()
                .deal(deal)
                .sourceType(DealSourceType.BASE_GAME)
                .number(groupNumber)
                .mediatype("image")
                .mimetype("image/jpeg")
                .media(imageUrl)
                .caption(caption)
                .build();
    }

    private Message buildMessageFromPackageData(Deal deal, SteamPackageDataDTO data) {

        String storeUrl = "https://store.steampowered.com/sub/" + deal.getSteamAppId();

        String imageUrl = data.headerImage();

        String caption = templateBuilder.buildCaptionForPackage(data, storeUrl);

        return Message.builder()
                .deal(deal)
                .sourceType(DealSourceType.PACKAGE)
                .number(groupNumber)
                .mediatype("image")
                .mimetype("image/jpeg")
                .media(imageUrl)
                .caption(caption)
                .build();
    }
}
