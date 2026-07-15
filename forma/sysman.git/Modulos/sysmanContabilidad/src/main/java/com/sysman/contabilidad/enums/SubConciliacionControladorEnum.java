/*
 * SubConciliacionControladorEnum
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
public enum SubConciliacionControladorEnum {

    PARAM13("ENTREGADO"),

    PARAM12("NUMERO_ANO"),

    PARAM11("NITTERCERO"),

    PARAM10("NIVEL"),

    PARAM9("MODULO"),

    PARAM8("TIPOPARAMETRO"),

    PARAM7("FECHA_INICIAL"),

    PARAM6("FECHA_CONCILIACION"),

    PARAM5("ANO_PERIODO"),

    PARAM4("MES_PERIODO"),

    PARAM3("CONCILIADOR"),

    TABLA("DETALLE_COMPROBANTE_CNT"),

    PARAM1("ULTIMODIA"),

    PARAM2("CLASESCONTABLES"),

    PARAM0("MES");

    private final String value;

    private SubConciliacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
