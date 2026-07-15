/*
 * AseoUrbanoControladorUrlEnum
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
public enum AseoUrbanoControladorUrlEnum {

    URL9911("ASEOURBANOCONTROLADORURL9911", "295001"),

    URL7759("ASEOURBANOCONTROLADORURL7759", "295002"),

    URL23167("ASEOURBANOCONTROLADORURL23167", "295003");

    private final String key;
    private final String value;

    private AseoUrbanoControladorUrlEnum(String key, String value) {
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
