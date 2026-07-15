/*
 * ChequesAnuladosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ChequesAnuladosControladorUrlEnum {

    URL7157("CHEQUESANULADOSCONTROLADORURL7157",
                    "53003"), URL5858(
                                    "CHEQUESANULADOSCONTROLADORURL5858",
                                    "53001"), URL5478(
                                                    "CHEQUESANULADOSCONTROLADORURL5478",
                                                    "4001"), URL6561(
                                                                    "CHEQUESANULADOSCONTROLADORURL6561",
                                                                    "53001"), URL334(
                                                                                    "CHEQUESANULADOSCONTROLADORURL334",
                                                                                    "39014");

    private final String key;
    private final String value;

    private ChequesAnuladosControladorUrlEnum(String key, String value) {
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
