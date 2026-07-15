/*
 * PredialRecCajasControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum PredialRecCajasControladorEnum {
    ANOPAGO("ANOPAGO"), REPORTE001415("001415FORMATOPNUDRECIBOPAGO"), NIT("NIT"), CODIGOPREDIO("CODIGOPREDIO"), NUMERO_ORDEN_PREDIAL(
                    "NUMERO_ORDEN_PREDIAL");

    private final String value;

    private PredialRecCajasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
