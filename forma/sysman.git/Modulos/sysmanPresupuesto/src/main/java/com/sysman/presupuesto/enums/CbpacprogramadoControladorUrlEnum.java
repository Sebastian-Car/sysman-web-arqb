/*
 * CbpacprogramadoControladorUrlEnum
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
public enum CbpacprogramadoControladorUrlEnum {

    URL4096("CBPACPROGRAMADOCONTROLADORURL4096",
                    "94036"), URL6572(
                                    "CBPACPROGRAMADOCONTROLADORURL6572",
                                    "75012"), URL4841(
                                                    "CBPACPROGRAMADOCONTROLADORURL4841",
                                                    "94034"), URL5704(
                                                                    "CBPACPROGRAMADOCONTROLADORURL5704",
                                                                    "75010"), URL7557(
                                                                                    "CBPACPROGRAMADOCONTROLADORURL7557",
                                                                                    "25014"), URL3815(
                                                                                                    "CBPACPROGRAMADOCONTROLADORURL3815",
                                                                                                    "4001");

    private final String key;
    private final String value;

    private CbpacprogramadoControladorUrlEnum(String key, String value) {
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
