/*
 * CriticaLecturaControladorUrlEnum
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
public enum CriticaLecturaControladorUrlEnum {

    URL0001("CRITICALECTURACONTROLADORURL0001", "213004"),

    URL0002("CRITICALECTURACONTROLADORURL0002", "213006"),

    URL5855("CRITICALECTURACONTROLADORURL5855", "214042");

    private final String key;
    private final String value;

    private CriticaLecturaControladorUrlEnum(String key, String value) {
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
