/*
 * LibroRegistroIngresosControladorUrlEnum
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
public enum LibroRegistroIngresosControladorUrlEnum {

    URL8783("LIBROREGISTROINGRESOSCONTROLADORURL8783", "20011"),

    URL10111("LIBROREGISTROINGRESOSCONTROLADORURL10111", "23019"),

    URL5337("LIBROREGISTROINGRESOSCONTROLADORURL5337", "7018"),

    URL4923("LIBROREGISTROINGRESOSCONTROLADORURL4923", "7007"),

    URL5821("LIBROREGISTROINGRESOSCONTROLADORURL5821", "4001"),

    URL8079("LIBROREGISTROINGRESOSCONTROLADORURL8079", "20013"),

    URL6160("LIBROREGISTROINGRESOSCONTROLADORURL6160", "45002"),

    URL9499("LIBROREGISTROINGRESOSCONTROLADORURL9499", "23010"),

    URL7060("LIBROREGISTROINGRESOSCONTROLADORURL7060", "45004");

    private final String key;
    private final String value;

    private LibroRegistroIngresosControladorUrlEnum(String key, String value) {
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
