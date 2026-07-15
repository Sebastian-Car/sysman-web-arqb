/*
 * IcuentaalmacendeprecControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum IcuentaalmacendeprecControladorUrlEnum {

    URL4758("ICUENTAALMACENDEPRECCONTROLADORURL4758", "102005"),

    URL4759("ICUENTAALMACENDEPRECCONTROLADORURL4759", "102006"),

    URL5084("ICUENTAALMACENDEPRECCONTROLADORURL5084", "112028"),

    URL6072("ICUENTAALMACENDEPRECCONTROLADORURL6072", "112030");

    private final String key;
    private final String value;

    private IcuentaalmacendeprecControladorUrlEnum(String key, String value) {
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
