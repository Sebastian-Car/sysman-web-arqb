/*
 * LiscomppptalesControladorUrlEnum
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
public enum LiscomppptalesControladorUrlEnum {

    URL4589("LISCOMPPPTALESCONTROLADORURL4589", "25012"),

    URL5292("LISCOMPPPTALESCONTROLADORURL5292", "25020"),

    URL6866("LISCOMPPPTALESCONTROLADORURL6866", "25022"),

    URL3966("LISCOMPPPTALESCONTROLADORURL3966", "25008");

    private final String key;
    private final String value;

    private LiscomppptalesControladorUrlEnum(String key, String value) {
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
