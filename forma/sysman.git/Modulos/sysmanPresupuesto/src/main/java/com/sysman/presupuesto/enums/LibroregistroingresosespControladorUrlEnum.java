/*
 * LibroregistroingresosespControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
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
public enum LibroregistroingresosespControladorUrlEnum {

    URL11137("LIBROREGISTROINGRESOSESPCONTROLADORURL11137", "94064"),

    URL9385("LIBROREGISTROINGRESOSESPCONTROLADORURL9385", "4001"),

    URL9753("LIBROREGISTROINGRESOSESPCONTROLADORURL9753", "7007"),

    URL10219("LIBROREGISTROINGRESOSESPCONTROLADORURL10219", "94062");

    private final String key;
    private final String value;

    private LibroregistroingresosespControladorUrlEnum(String key,
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
