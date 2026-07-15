/*
 * PredialcertcatastralControladorEnum
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
public enum PredialcertcatastralControladorEnum {

    PARAM0("TIPO"),

    PARAM1("NUMCER"),

    PARAM2("DIRECCION"),

    PARAM3("UBICACION"),

    PARAM4("AREA_HA"),

    PARAM5("AREA_M2"),

    PARAM6("AVALUO"),

    PARAM7("ANOAVALUO"),

    PARAM8("NUMERO_ORDEN_PREDIAL"),

    PARAM9("NIT"),

    PARAM11("FECHA_EXP"),

    PARAM12("CODIGO_PREDIO"),

    PARAM13("SUCURSAL"),

    PARAM14("CODIGOPREDIO"),

    PARAM15("TIPOCERTIFICADO");

    private final String value;

    private PredialcertcatastralControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
