package com.PedroNunesDev.PromoGamer.mapper;

import com.PedroNunesDev.PromoGamer.dto.MessageDtoResponse;
import com.PedroNunesDev.PromoGamer.model.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DealMapper.class})
public interface MessageMapper {

    MessageDtoResponse toDTO (Message message);
}
