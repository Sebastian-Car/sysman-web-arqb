/*
 * MultiusuariosControladorUrlEnum
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
public enum MultiusuariosControladorUrlEnum {

    URL6791("MULTIUSUARIOSCONTROLADORURL6791", "242005"),

    URL8121("MULTIUSUARIOSCONTROLADORURL8121", "310005"),

    URL7292("MULTIUSUARIOSCONTROLADORURL7292", "310005"),

    URL12753("MULTIUSUARIOSCONTROLADORURL12753", "213140");

    private final String key;
    private final String value;

    private MultiusuariosControladorUrlEnum(String key, String value) {
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
