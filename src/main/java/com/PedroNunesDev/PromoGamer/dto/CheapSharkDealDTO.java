package com.PedroNunesDev.PromoGamer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheapSharkDealDTO(
        String title,
        String dealID,
        @JsonProperty("steamAppID") String steamAppId,
        String steamRatingPercent,
        Long lastChange
) {
}