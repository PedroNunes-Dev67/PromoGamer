package com.PedroNunesDev.PromoGamer.service;

import com.PedroNunesDev.PromoGamer.dto.MessageDtoResponse;
import com.PedroNunesDev.PromoGamer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageScheduledService {

    private final MessageService messageService;

    @Scheduled(cron = "0 0/10 12-13 * * *", zone = "America/Recife")
    public void executarNaJanelaMeioDia(){
        executarEnvioDeMensagem();
    }

    @Scheduled(cron = "0 0/10 18-20 * * *", zone = "America/Recife")
    public void executarNaJanelaNoite(){
        executarEnvioDeMensagem();
    }

    public void executarEnvioDeMensagem(){

        MessageDtoResponse response = null;
        long tentativas = 0;

        while (response == null && tentativas < 5){

            try{
                response = messageService.saveNewMessage();
            }
            catch (ResourceNotFoundException e){
                log.info("Nenhuma deal pendente restante, encerrando ciclo");
                return;
            }

            tentativas++;
        }
    }
}
