/*
 * ListadoSinMedicionAgrupadoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ListadoSinMedicionAgrupadoControladorUrlEnum {

    URL5235("LISTADOSINMEDICIONAGRUPADOCONTROLADORURL5235",
                    "227007"),

    URL6290("LISTADOSINMEDICIONAGRUPADOCONTROLADORURL6290",
                    "214042"),

    URL7127("LISTADOSINMEDICIONAGRUPADOCONTROLADORURL7127",
                    "234001"),

    URL5910("LISTADOSINMEDICIONAGRUPADOCONTROLADORURL5910",
                    "227001"),

    URL8100("LISTADOSINMEDICIONAGRUPADOCONTROLADORURL8100",
                    "234003"),

    URL9053("LISTADOSINMEDICIONAGRUPADOCONTROLADORURL9053",
                    "214024");

    private final String key;
    private final String value;

    private ListadoSinMedicionAgrupadoControladorUrlEnum(String key,
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
