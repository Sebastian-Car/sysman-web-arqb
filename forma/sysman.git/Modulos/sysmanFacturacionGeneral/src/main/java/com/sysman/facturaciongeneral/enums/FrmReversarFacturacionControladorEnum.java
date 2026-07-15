/*
 * FrmReversarFacturacionControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmReversarFacturacionControladorEnum {

    TIPOCOBRO("TIPOCOBRO"),

    NUMERO_FACTURA("NUMERO_FACTURA"),

    CODIGO_COBRO("CODIGO_COBRO");

    private final String value;

    private FrmReversarFacturacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
