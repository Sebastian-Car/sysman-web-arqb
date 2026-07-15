/*
 * LibroDiarioOficialControladorUrlEnum
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
public enum LibroDiarioOficialControladorUrlEnum {

    URL6680("LIBRODIARIOOFICIALCONTROLADORURL6680", "7008"),

    URL6311("LIBRODIARIOOFICIALCONTROLADORURL6311", "4001"),

    URL10158("LIBRODIARIOOFICIALCONTROLADORURL10158", "20015"),

    URL7139("LIBRODIARIOOFICIALCONTROLADORURL7139", "16008"), // 29027

    URL8203("LIBRODIARIOOFICIALCONTROLADORURL8203", "16010"), // 29029

    URL9410("LIBRODIARIOOFICIALCONTROLADORURL9410", "20013");

    private final String key;
    private final String value;

    private LibroDiarioOficialControladorUrlEnum(String key, String value) {
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
