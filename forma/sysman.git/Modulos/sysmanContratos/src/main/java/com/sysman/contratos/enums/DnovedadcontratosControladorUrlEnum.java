/*
 * DnovedadcontratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DnovedadcontratosControladorUrlEnum {

    URL8453("DNOVEDADCONTRATOSCONTROLADORURL8453", "429003"),
    URL5971("DNOVEDADCONTRATOSCONTROLADORURL5971", "112068");

    private final String key;
    private final String value;

    private DnovedadcontratosControladorUrlEnum(String key, String value) {
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
