/*
 * ServiciosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ServiciosControladorUrlEnum {

    URL3320("SERVICIOSCONTROLADORURL3320", "112070"),

    URL4390("SERVICIOSCONTROLADORURL4390", "112073"),

    URL3036("SERVICIOSCONTROLADORURL3036", "155003"),

    URL3037("SERVICIOSCONTROLADORURL3037", "112075"),

    URL3038("SERVICIOSCONTROLADORURL3038", "112076");

    private final String key;
    private final String value;

    private ServiciosControladorUrlEnum(String key, String value) {
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
