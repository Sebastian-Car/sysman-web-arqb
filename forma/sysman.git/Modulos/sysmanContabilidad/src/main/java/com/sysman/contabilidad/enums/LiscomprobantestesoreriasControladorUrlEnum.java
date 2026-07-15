/*
 * LiscomprobantestesoreriasControladorUrlEnum
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
public enum LiscomprobantestesoreriasControladorUrlEnum {

    URL3168("LISCOMPROBANTESTESORERIASCONTROLADORURL3168", "15024"),

    URL5269("LISCOMPROBANTESTESORERIASCONTROLADORURL5269", "14001"),

    URL4135("LISCOMPROBANTESTESORERIASCONTROLADORURL4135", "15026"),

    URL5871("LISCOMPROBANTESTESORERIASCONTROLADORURL5871", "14033");

    private final String key;
    private final String value;

    private LiscomprobantestesoreriasControladorUrlEnum(String key,
        String value) {
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
