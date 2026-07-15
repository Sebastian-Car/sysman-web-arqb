/*
 * AuxiliarRecaudosUsoEstraControladorUrlEnum
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
public enum AuxiliarRecaudosUsoEstraControladorUrlEnum {

    URL5802("AUXILIARRECAUDOSUSOESTRACONTROLADORURL5802", "214032"),

    URL0001("AUXILIARRECAUDOSUSOESTRACONTROLADORURL0001", "213031"),

    URL0002("AUXILIARRECAUDOSUSOESTRACONTROLADORURL0002", "213033");

    private final String key;
    private final String value;

    private AuxiliarRecaudosUsoEstraControladorUrlEnum(String key,
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
