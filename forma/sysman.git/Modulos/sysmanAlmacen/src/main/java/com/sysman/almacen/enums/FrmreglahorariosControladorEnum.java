/*
 * FrmreglahorariosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmreglahorariosControladorEnum {

    PARAM4("SERIE_DEVOLUTIVO"),

    PARAM3("ELEMENTO_DEVOLUTIVO"),

    PARAM1("SERIEACTUAL"),

    PARAM0("ELEMENTOACTUAL"),

    PARAM2("REGLA");

    private final String value;

    private FrmreglahorariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
