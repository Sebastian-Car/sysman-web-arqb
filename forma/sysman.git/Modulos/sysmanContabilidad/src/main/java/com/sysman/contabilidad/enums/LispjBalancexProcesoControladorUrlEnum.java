/*
 * LispjBalancexProcesoControladorUrlEnum
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
public enum LispjBalancexProcesoControladorUrlEnum {

    URL0001("LISPJBALANCEXPROCESOCONTROLADORURL0001", "16005"),
    URL0002("LISPJBALANCEXPROCESOCONTROLADORURL0002", "16003"),
    URL0003("LISPJBALANCEXPROCESOCONTROLADORURL0003", "14036"),
    URL0004("LISPJBALANCEXPROCESOCONTROLADORURL0004", "14038"),
    URL0005("LISPJBALANCEXPROCESOCONTROLADORURL0005", "1935001"),
    URL0006("LISPJBALANCEXPROCESOCONTROLADORURL0006", "1935003");

    private final String key;
    private final String value;

    private LispjBalancexProcesoControladorUrlEnum(String key, String value) {
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
