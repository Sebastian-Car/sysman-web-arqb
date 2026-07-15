/*
 * DetalleDocumentoAnexosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum DetalleDocumentoAnexosControladorEnum {

    TIPO_TRANSACCION("TIPO_TRANSACCION"),

    KEY_TIPO_TRANSACCION("KEY_TIPO_TRANSACCION"),

    NUMERO_TRANSACCION("NUMERO_TRANSACCION"),

    TIPO_DOCUMENTO("TIPO_DOCUMENTO"),

    NOMBRETIPODOCUMENTO("NOMBRETIPODOCUMENTO"),

    KEY_CONSECUTIVO("KEY_CONSECUTIVO");

    private final String value;

    private DetalleDocumentoAnexosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
