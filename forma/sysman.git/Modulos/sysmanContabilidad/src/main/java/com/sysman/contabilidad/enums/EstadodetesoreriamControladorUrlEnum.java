/*
 * EstadodetesoreriamControladorUrlEnum
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
public enum EstadodetesoreriamControladorUrlEnum {

    URL3757("ESTADODETESORERIAMCONTROLADORURL3757", "4002"),

    URL4237("ESTADODETESORERIAMCONTROLADORURL4237", "7007"),

    URL4921("ESTADODETESORERIAMCONTROLADORURL4921", "29114"),

    URL5827("ESTADODETESORERIAMCONTROLADORURL5827", "29116");

    private final String key;
    private final String value;

    private EstadodetesoreriamControladorUrlEnum(String key, String value) {
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
