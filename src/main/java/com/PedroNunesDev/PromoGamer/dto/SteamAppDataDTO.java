package com.PedroNunesDev.PromoGamer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamAppDataDTO(
        String name,
        @JsonProperty("steam_appid") Long steamAppId,
        @JsonProperty("header_image") String headerImage,
        @JsonProperty("price_overview") SteamPriceOverviewDTO priceOverview
) {}