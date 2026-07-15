/*
 * LislibrodiariosControladorUrlEnum
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
public enum LislibrodiariosControladorUrlEnum {

    URL5929("LISLIBRODIARIOSCONTROLADORURL5929", "16005"),

    URL4932("LISLIBRODIARIOSCONTROLADORURL4932", "4001"),

    URL5293("LISLIBRODIARIOSCONTROLADORURL5293", "7007"),

    URL7222("LISLIBRODIARIOSCONTROLADORURL7222", "16003");

    private final String key;
    private final String value;

    private LislibrodiariosControladorUrlEnum(String key, String value) {
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
