/*
 * BalancePruebaNiifControladorUrlEnum
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
public enum BalancePruebaNiifControladorUrlEnum {
    URL5994("BALANCEPRUEBANIIFCONTROLADORURL5994", "4001"),

    URL5995("BALANCEPRUEBANIIFCONTROLADORURL5995", "7007"),

    URL5996("BALANCEPRUEBANIIFCONTROLADORURL5996", "7004"),

    URL4721("BALANCEPRUEBANIIFCONTROLADORURL4721", "16003"),

    URL3385("BALANCEPRUEBANIIFCONTROLADORURL3385", "16005");

    private final String key;
    private final String value;

    private BalancePruebaNiifControladorUrlEnum(String key, String value) {
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
