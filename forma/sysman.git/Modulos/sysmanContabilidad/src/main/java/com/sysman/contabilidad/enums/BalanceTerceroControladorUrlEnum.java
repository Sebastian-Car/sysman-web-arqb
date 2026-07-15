/*
 * BalanceTerceroControladorUrlEnum
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
public enum BalanceTerceroControladorUrlEnum {

    URL10424("BALANCETERCEROCONTROLADORURL10424", "14036"),

    URL11242("BALANCETERCEROCONTROLADORURL11242", "14038"),

    URL7549("BALANCETERCEROCONTROLADORURL7549", "29027"),

    URL8900("BALANCETERCEROCONTROLADORURL8900", "29029"),

    URL6989("BALANCETERCEROCONTROLADORURL6989", "4001");

    private final String key;
    private final String value;

    private BalanceTerceroControladorUrlEnum(String key, String value) {
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
