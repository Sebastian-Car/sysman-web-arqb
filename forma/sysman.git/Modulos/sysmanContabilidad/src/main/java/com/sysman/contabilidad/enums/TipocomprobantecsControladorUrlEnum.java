/*
 * TipocomprobantecsControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum TipocomprobantecsControladorUrlEnum {

    URL5587("TIPOCOMPROBANTECSCONTROLADORURL5587", "15019"),

    URL4484("TIPOCOMPROBANTECSCONTROLADORURL4484", "21001"),

    URL5012("TIPOCOMPROBANTECSCONTROLADORURL5012", "21002"),

    URL4194("TIPOCOMPROBANTECSCONTROLADORURL4194", "6001"),

    URL6151("TIPOCOMPROBANTECSCONTROLADORURL6151", "25007");

    private final String key;
    private final String value;

    private TipocomprobantecsControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
