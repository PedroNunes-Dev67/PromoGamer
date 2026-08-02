package com.PedroNunesDev.PromoGamer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO responsável por captar os dados detalhados dos jogos, onde estou usando um tipo genérico que irá usar tanto para apps quanto para packages
 *
 * @param success
 * @param data
 * @param <T>
 *
 * @author Pedro Nunes Dev
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamDetailsWrapper<T>(
        boolean success,
        T data
) {}