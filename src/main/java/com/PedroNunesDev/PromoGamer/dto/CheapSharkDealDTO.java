package com.PedroNunesDev.PromoGamer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheapSharkDealDTO(
        String internalName,
        String title,
        String dealID,
        String storeID,
        String gameID,
        String salePrice,
        String normalPrice,
        String isOnSale,
        String savings,
        String metacriticScore,
        String steamRatingText,
        String steamRatingPercent,
        String steamRatingCount,
        @JsonProperty("steamAppID") String steamAppId,
        Long releaseDate,
        Long lastChange,
        String dealRating,
        String thumb
) {
}
