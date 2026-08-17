package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.MessageDtoResponse;
import com.PedroNunesDev.PromoGamer.enums.DealEnumStatus;
import com.PedroNunesDev.PromoGamer.exception.ResourceNotFoundException;
import com.PedroNunesDev.PromoGamer.mapper.MessageMapper;
import com.PedroNunesDev.PromoGamer.model.Deal;
import com.PedroNunesDev.PromoGamer.model.Message;
import com.PedroNunesDev.PromoGamer.repository.DealRepository;
import com.PedroNunesDev.PromoGamer.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final DealRepository dealRepository;
    private final SteamService steamService;
    private final MessageMapper messageMapper;

    @Transactional
    public MessageDtoResponse saveNewMessage(){

        log.info("Iniciando salvamento de nova mensagem para envio...");

        Deal deal = dealRepository.findFirstByDealEnumStatus(DealEnumStatus.PENDENTE)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum promoção para envio encontrada"));

        Message message = steamService.buildMessageFromDeal(deal)
                .orElseThrow(() -> new RuntimeException("Ocorreu um erro ao construir mensagem de envio"));

        Message novaMessage = messageRepository.save(message);

        deal.updateStatus(DealEnumStatus.PROCESSADO);
        dealRepository.save(deal);

        return messageMapper.toDTO(novaMessage);
    }
}
