package com.PedroNunesDev.PromoGamer.mapper;

import com.PedroNunesDev.PromoGamer.dto.DealDtoResponse;
import com.PedroNunesDev.PromoGamer.model.Deal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DealMapper {

    DealDtoResponse toDTO(Deal deal);
}
