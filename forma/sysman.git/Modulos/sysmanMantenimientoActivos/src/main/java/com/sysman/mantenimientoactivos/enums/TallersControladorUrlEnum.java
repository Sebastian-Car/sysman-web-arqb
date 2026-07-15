/*
 * TallersControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum TallersControladorUrlEnum {

    URL7385("TALLERSCONTROLADORURL7385",
                    "14001"),

    URL8482("TALLERSCONTROLADORURL8482",
                    "44500U"),

    URL10832("TALLERSCONTROLADORURL10832",
                    "44500D"),

    URL5279("TALLERSCONTROLADORURL5279",
                    "14001"),

    URL7441("TALLERSCONTROLADORURL7441",
                    "44500C"),

    URL6573("TALLERSCONTROLADORURL6573",
                    "14106"),

    URL4042("TALLERSCONTROLADORURL4042",
                    "445005");

    private final String key;
    private final String value;

    private TallersControladorUrlEnum(String key, String value) {
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
