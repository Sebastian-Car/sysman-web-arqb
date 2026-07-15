/*
 * DiarioSaldosBancariosControladorEnum
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
public enum MovimientosCuentasControladorEnum {

    TABLA("DETALLE_COMPROBANTE_CNT"), 
    PARAM0("MESINICIAL"), 
    PARAM1("MESFINAL"),  
    CREDITO("TOTAL_CREDITO"),
    DEBITO("TOTAL_DEBITO");

    private final String value;

    private MovimientosCuentasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
