/*
 * SubpactesoreriasControladorUrlEnum
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
public enum SubpactesoreriasControladorUrlEnum {

    URL6213("SUBPACTESORERIASCONTROLADORURL6213",
                    "94038"), URL11380(
                                    "SUBPACTESORERIASCONTROLADORURL11380",
                                    "98005"), URL118(
                                                    "SUBPACTESORERIASCONTROLADORURL118",
                                                    "98001"), URL124(
                                                                    "SUBPACTESORERIASCONTROLADORURL124",
                                                                    "98003"), URL202(
                                                                                    "SUBPACTESORERIASCONTROLADORURL202",
                                                                                    "98004");

    private final String key;
    private final String value;

    private SubpactesoreriasControladorUrlEnum(String key, String value) {
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
