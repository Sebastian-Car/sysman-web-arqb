/*
 * EntidadesCapacitacionControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EntidadesCapacitacionControladorUrlEnum {

    URL4780("ENTIDADESCAPACITACIONCONTROLADORURL4780", "14152"),

    URL0001("ENTIDADESCAPACITACIONCONTROLADORURL0001", "14096");

    private final String key;
    private final String value;

    private EntidadesCapacitacionControladorUrlEnum(String key, String value) {
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
