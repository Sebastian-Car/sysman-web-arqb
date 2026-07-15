/*
 * LisejecpptalgastosfldisControladorUrlEnum
 *
 * 1.0
 *
 * 19/10/2020
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;
/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */

public enum LisejecpptalgastosfldisControladorUrlEnum {
	URL8754("LISEJECPPTALGASTOSFLDISCONTROLADORURLENUM8754", "23019"),

    URL6925("LISEJECPPTALGASTOSFLDISCONTROLADORURLENUM6925", "94066"),

    URL5230("LISEJECPPTALGASTOSFLDISCONTROLADORURLENUM5230", "7007"),

    URL7986("LISEJECPPTALGASTOSFLDISCONTROLADORURLENUM7986", "23010"),

    URL5961("LISEJECPPTALGASTOSFLDISCONTROLADORURLENUM5961", "94052"),

    URL4777("LISEJECPPTALGASTOSFLDISCONTROLADORURLENUM4777", "4001");

    private final String key;
    private final String value;

    private LisejecpptalgastosfldisControladorUrlEnum(String key, String value) {
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