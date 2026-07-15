/*
 * BalanceVerificacionControladorUrlEnum
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
public enum BalanceVerificacionControladorUrlEnum {

    URL3728("BALANCEVERIFICACIONCONTROLADORURL3728", "29007"),

    URL4685("BALANCEVERIFICACIONCONTROLADORURL4685", "29009"),

    URL3194("BALANCEVERIFICACIONCONTROLADORURL3194", "4001");

    private final String key;
    private final String value;

    private BalanceVerificacionControladorUrlEnum(String key, String value) {
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
