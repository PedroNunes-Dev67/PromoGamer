package com.PedroNunesDev.PromoGamer.enums;

public enum MessageStatus {

    PENDENTE("PENDENTE"),
    ENVIADA("ENVIADA"),
    FALHA("FALHA");

    private String type;

    MessageStatus(String type) {
        this.type = type;
    }

    public static MessageStatus from(String typeStatusVerify){

        MessageStatus messageStatus = null;

        for (MessageStatus messageStatusObject : values()){
            if (messageStatusObject.type.equalsIgnoreCase(typeStatusVerify)){
                messageStatus = messageStatusObject;
            }
        }

        if (messageStatus == null){
            throw new IllegalArgumentException("Not found deal status");
        }

        return messageStatus;
    }
}
