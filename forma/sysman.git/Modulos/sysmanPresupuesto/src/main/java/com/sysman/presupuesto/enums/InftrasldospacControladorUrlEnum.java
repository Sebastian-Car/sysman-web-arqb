/*
 * InftrasldospacControladorUrlEnum
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
public enum InftrasldospacControladorUrlEnum {

    URL3628("INFTRASLDOSPACCONTROLADORURL3628", "4001"),

    URL4010("INFTRASLDOSPACCONTROLADORURL4010", "94058"),

    URL4736("INFTRASLDOSPACCONTROLADORURL4736", "94060");

    private final String key;
    private final String value;

    private InftrasldospacControladorUrlEnum(String key, String value) {
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
