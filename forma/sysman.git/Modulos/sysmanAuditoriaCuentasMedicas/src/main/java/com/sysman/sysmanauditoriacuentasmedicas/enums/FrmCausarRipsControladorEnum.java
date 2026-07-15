/*
 * ActualizarSaldosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.sysmanauditoriacuentasmedicas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmCausarRipsControladorEnum {

    TIPO_COMPROBANTE("TIPO_COMPROBANTE"),

    NOMBRE_TIPO_COMPROBANTE("NOMBRE_TIPO_COMPROBANTE"),

    CAUSADO("CAUSADO"),

    CAUSADO_FECHA("CAUSADO_FECHA"),

    APROBADO("APROBADO"),

    NUM_FACTURA("NUM_FACTURA"),

    COD_PREST_SERV_SALUD("COD_PREST_SERV_SALUD"),
    
    NUM_IDENTIF_PRESTADOR("NUM_IDENTIF_PRESTADOR"), // 7715707 MPEREZ

    RADICADO("RADICADO"),
    
    NUMERO_COMP("NUMERO_COMP");

    private final String value;

    private FrmCausarRipsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
