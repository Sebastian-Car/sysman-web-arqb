/*
 * EliminarComprobanteControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum EliminarComprobanteControladorEnum {
    CREDITOSAFECTADOS("CREDITOSAFECTADOS"),

    DEBITOSAFECTADOS("DEBITOSAFECTADOS"),

    CONSECUTIVOAFECTADO("CONSECUTIVOAFECTADO"),

    VALOR_CREDITO("VALOR_CREDITO"),

    VALOR_DEBITO("VALOR_DEBITO"),

    TIPO_CPTE_AFECT("TIPO_CPTE_AFECT"),

    CMPTE_AFECTADO("CMPTE_AFECTADO"),

    TIPO("TIPO");

    private final String value;

    private EliminarComprobanteControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
