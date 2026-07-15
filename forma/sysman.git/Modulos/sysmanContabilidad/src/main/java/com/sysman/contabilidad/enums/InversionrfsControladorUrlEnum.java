/*
 * InversionrfsControladorUrlEnum
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
public enum InversionrfsControladorUrlEnum {

    URL5207("INVERSIONRFSCONTROLADORURL5207", "36001"), URL4688(
                    "INVERSIONRFSCONTROLADORURL4688",
                    "77001"), URL4926("INVERSIONRFSCONTROLADORURL4926",
                                    "66001"), URL3905(
                                                    "INVERSIONRFSCONTROLADORURL3905",
                                                    "34005"), URL226(
                                                                    "INVERSIONRFSCONTROLADORURL226",
                                                                    "79001");

    private final String key;
    private final String value;

    private InversionrfsControladorUrlEnum(String key, String value) {
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
