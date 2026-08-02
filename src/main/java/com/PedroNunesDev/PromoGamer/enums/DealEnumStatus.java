package com.PedroNunesDev.PromoGamer.enums;

public enum DealEnumStatus {

    PENDENTE("PENDENTE"),
    PROCESSADO("PROCESSADO"),
    CONCLUIDO("CONCLUIDO"),
    IGNORADO("IGNORADO");

    private String type;

    DealEnumStatus(String type) {
        this.type = type;
    }

    public static DealEnumStatus from(String typeStatusVerify){

        DealEnumStatus dealEnumStatus = null;

        for (DealEnumStatus dealEnumStatusObject : values()){
            if (dealEnumStatusObject.type.equalsIgnoreCase(typeStatusVerify)){
                dealEnumStatus = dealEnumStatusObject;
            }
        }

        if (dealEnumStatus == null){
            throw new IllegalArgumentException("Not found deal status");
        }

        return dealEnumStatus;
    }
}
