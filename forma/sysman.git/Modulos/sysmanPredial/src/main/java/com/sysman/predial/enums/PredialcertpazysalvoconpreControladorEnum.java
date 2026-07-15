/*
 * PredialcertpazysalvoconpreControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum PredialcertpazysalvoconpreControladorEnum {

    TIPO("TIPO"),

    CODIGOPREDIO("CODIGOPREDIO"),

    NUMCER("NUMCER"),

    REC_VALORIZACION("REC_VALORIZACION"),

    FECHA_EXP("FECHA_EXP"),

    AREA_HA("AREA_HA"),

    AREA_M2("AREA_M2"),

    ANOAVALUO("ANOAVALUO"),

    NIT("NIT"),

    PAGO_ANO("PAGO_ANO"),

    PAG_FEC("PAG_FEC"),;

    private final String value;

    private PredialcertpazysalvoconpreControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
