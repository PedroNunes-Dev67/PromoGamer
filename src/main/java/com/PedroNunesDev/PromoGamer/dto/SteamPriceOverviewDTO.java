package com.PedroNunesDev.PromoGamer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public record SteamPriceOverviewDTO(
        String currency,
        Integer initial,
        @JsonProperty("final") Integer finalPrice,
        @JsonProperty("discount_percent") Integer discountPercent,
        @JsonProperty("initial_formatted") String initialFormatted,
        @JsonProperty("final_formatted") String finalFormatted
) {}