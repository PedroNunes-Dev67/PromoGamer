package com.PedroNunesDev.PromoGamer.enums;

public enum DealSourceType {

    BASE_GAME("BASE_GAME"),
    PACKAGE("PACKAGE");

    private String type;

    DealSourceType(String type) {
        this.type = type;
    }

    public static DealSourceType from(String typeStatusVerify){

        DealSourceType dealSourceType = null;

        for (DealSourceType dealsSourceTypeObject : values()){
            if (dealsSourceTypeObject.type.equalsIgnoreCase(typeStatusVerify)){
                dealSourceType = dealsSourceTypeObject;
            }
        }

        if (dealSourceType == null){
            throw new IllegalArgumentException("Not found deal status");
        }

        return dealSourceType;
    }
}
