/*
 * PeriodoEtapaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PeriodoEtapaControladorUrlEnum {

    URL3282("PERIODOETAPACONTROLADORURL3282", "4001"),

    URL2704("PERIODOETAPACONTROLADORURL2704", "184006");

    private final String key;
    private final String value;

    private PeriodoEtapaControladorUrlEnum(String key, String value) {
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
