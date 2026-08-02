package com.PedroNunesDev.PromoGamer.controller;

import com.PedroNunesDev.PromoGamer.dto.MessageDtoResponse;
import com.PedroNunesDev.PromoGamer.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message/deals")
@Tag(name = "Message Controller", description = "Responsible for actions related to Message Deals")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDtoResponse> saveNewMessage(){

        MessageDtoResponse response = messageService.saveNewMessage();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
