/*
 * PagosdoblesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PagosdoblesControladorUrlEnum {

    URL001("PAGOSDOBLESCONTROLADORURL001", "381013")

    , URL8174("PAGOSDOBLESCONTROLADORURL8174", "367207")

    , URL8548("PAGOSDOBLESCONTROLADORURL8548", "367205")

    , URL7801("PAGOSDOBLESCONTROLADORURL7801", "4001");

    private final String key;
    private final String value;

    private PagosdoblesControladorUrlEnum(String key, String value) {
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
