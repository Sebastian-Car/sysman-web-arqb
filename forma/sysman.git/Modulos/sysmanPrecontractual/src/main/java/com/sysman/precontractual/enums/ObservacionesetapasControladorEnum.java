/*
 * ObservacionesetapasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ObservacionesetapasControladorEnum {

    CONSECUTIVOTX("CONSECUTIVOTX"),

    CONSECUTIVODET("CONSECUTIVODET"),

    TIPO_CONTRATO("TIPO_CONTRATO"),

    NIT("NIT"),

    TIPOCONTRATO("TIPOCONTRATO"),

    TRANSACCION("TRANSACCION"),

    CONSECUTIVODETALLE("CONSECUTIVODETALLE"),

    ID("ID"),

    OBSERVADOR("OBSERVADOR");

    private final String value;

    private ObservacionesetapasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
