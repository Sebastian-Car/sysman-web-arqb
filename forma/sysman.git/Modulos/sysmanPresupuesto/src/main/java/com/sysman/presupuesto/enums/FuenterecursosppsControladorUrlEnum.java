/*
 * FuenterecursosppsControladorUrlEnum
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
public enum FuenterecursosppsControladorUrlEnum {
    URL128("FUENTERECURSOSPPSCONTROLADORURL128", "34011"), URL4989(
                    "FUENTERECURSOSPPSCONTROLADORURL4989",
                    "4001"), URL4660(
                                    "FUENTERECURSOSPPSCONTROLADORURL4660",
                                    "126001"), URL5987(
                                                    "FUENTERECURSOSPPSCONTROLADORURL5987",
                                                    "4016"), URL119(
                                                                    "FUENTERECURSOSPPSCONTROLADORURL119",
                                                                    "34013"), URL122(
                                                                                    "FUENTERECURSOSPPSCONTROLADORURL122",
                                                                                    "34014"), URL126(
                                                                                                    "FUENTERECURSOSPPSCONTROLADORURL126",
                                                                                                    "3400D");

    private final String key;
    private final String value;

    private FuenterecursosppsControladorUrlEnum(String key, String value) {
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
