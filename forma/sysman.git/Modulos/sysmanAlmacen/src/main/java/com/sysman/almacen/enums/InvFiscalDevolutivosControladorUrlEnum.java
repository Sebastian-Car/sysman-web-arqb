/*
 * InvFiscalDevolutivosControladorUrlEnum
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
public enum InvFiscalDevolutivosControladorUrlEnum {

    URL4168("INVFISCALDEVOLUTIVOSCONTROLADORURL4168", "62025"),

    URL3726("INVFISCALDEVOLUTIVOSCONTROLADORURL3726", "62002"),

    URL131("INVFISCALDEVOLUTIVOSCONTROLADORURL131", "62019"),

    URL159("INVFISCALDEVOLUTIVOSCONTROLADORURL131", "62027");

    private final String key;
    private final String value;

    private InvFiscalDevolutivosControladorUrlEnum(String key, String value) {
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
