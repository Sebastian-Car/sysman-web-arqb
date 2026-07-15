/*
 * SeleccionarPlacasControladorEnum
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
public enum SeleccionarPlacasControladorEnum {

    PARAM9("MESESBODEGA"),

    PARAM8("CANTANTERIOR"),

    PARAM7("VALORBASE"),

    PARAM6("SALDOCANT"),

    PARAM5("CANTIDAD"),

    PARAM4("HORA"),

    PARAM3("TIPOELEMENTO"),

    PARAM1("MOVP"),

    PARAM2("CODIGOA"),

    PARAM0("TIPOMOVP"),

    VLRUNITARIO_ANTESIVA("VLRUNITARIO_ANTESIVA");

    private final String value;

    private SeleccionarPlacasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
