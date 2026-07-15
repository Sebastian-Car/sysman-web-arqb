/*
 * FrmActualizarFacturaControladorEnum
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
public enum FrmActualizarFacturaControladorEnum {

    ANIO("ANIO"),

    TIPO("TIPO"),

    TIPOFACTURA("TIPOFACTURA"),

    CLASECONTABLE("CLASECONTABLE"),

    TIPOCOBRO("TIPOCOBRO"),

    CLASECUENTA("CLASECUENTA"),

    ANODOC("ANODOC"),

    TIPODOC("TIPODOC"),

    NRODOC("NRODOC"),

    TIPO_ABONO("TIPO_ABONO"),
    
    CODIGO_ABONO("CODIGO_ABONO");

    private final String value;

    private FrmActualizarFacturaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
