package com.PedroNunesDev.PromoGamer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamPackagePriceDTO(
        String currency,
        Integer initial,
        @JsonProperty("final") Integer finalPrice,
        @JsonProperty("discount_percent") Integer discountPercent,
        Integer individual
) {}