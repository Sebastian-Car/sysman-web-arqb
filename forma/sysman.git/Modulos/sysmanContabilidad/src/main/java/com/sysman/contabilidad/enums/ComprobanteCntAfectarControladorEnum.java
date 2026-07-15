/*
 * ComprobanteCntAfectarControladorEnum
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
public enum ComprobanteCntAfectarControladorEnum {

    VALORDOC("VALORDOC"),

    TIPO("TIPO"),

    FECHARES("FECHARES"),

    CUENTAPPTAL("CUENTAPPTAL"),

    VALOR_DEBITO("VALOR_DEBITO"),

    VALOR_CREDITO("VALOR_CREDITO"),

    EJECUCION_DEBITO("EJECUCION_DEBITO"),

    EJECUCION_CREDITO("EJECUCION_CREDITO"),

    CADENA("CADENA"),

    CLASEAFECTAR("CLASEAFECTAR"),

    COMPRELACIONADO("COMPRELACIONADO"),

    ANOCOMPROBANTE("ANOCOMPROBANTE"),

    FECHACOMPROBANTE("FECHACOMPROBANTE"),

    TIPOCOMPROBANTE("TIPOCOMPROBANTE"),

    SUCURSALCOMPROBANTE("SUCURSALCOMPROBANTE"),

    TERCEROCOMPROBANTE("TERCEROCOMPROBANTE");

    private final String value;

    private ComprobanteCntAfectarControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
