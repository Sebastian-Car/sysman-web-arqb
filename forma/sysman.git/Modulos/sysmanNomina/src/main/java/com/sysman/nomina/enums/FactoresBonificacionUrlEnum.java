/*
 * FactoresBonificacionUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FactoresBonificacionUrlEnum {

    URL4061("FACTORESBONIFICACIONURL4061", "7024"),

    URL3516("FACTORESBONIFICACIONURL3516", "4001"),

    URL3517("FACTORESBONIFICACIONURL3517", "471027");

    private final String key;
    private final String value;

    private FactoresBonificacionUrlEnum(String key, String value) {
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
